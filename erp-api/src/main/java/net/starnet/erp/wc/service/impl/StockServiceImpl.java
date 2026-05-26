package net.starnet.erp.wc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.starnet.erp.constant.Define;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.service.ProductService;
import net.starnet.erp.wc.dao.StockDao;
import net.starnet.erp.wc.model.IssueProduct;
import net.starnet.erp.wc.model.Stock;
import net.starnet.erp.wc.model.StockRecord;
import net.starnet.erp.wc.model.TransferProduct;
import net.starnet.erp.wc.service.StockRecordService;
import net.starnet.erp.wc.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockServiceImpl extends ServiceImpl<StockDao, Stock> implements StockService {

    @Autowired
    private ProductService productService;
    @Autowired
    private StockRecordService recordService;
    @Autowired
    private StockRecordService stockRecordService;

    @Override
    public Stock findByProductAndWarehouse(String productId, String warehouseId) {
        return getOne(new QueryWrapper<Stock>().eq("productId", productId).eq("warehouseId", warehouseId));
    }

    @Override
    public Stock handleStock(IssueProduct issueProduct, String stockType) {
        Stock stock = findByProductAndWarehouse(issueProduct.getProductId(), issueProduct.getWarehouseId());

        if (stock == null) {
            stock = new Stock();
            stock.setProductId(issueProduct.getProductId());
            stock.setWarehouseId(issueProduct.getWarehouseId());
        }

        // 设置 数量
        stock.setQuantity(Define.STOCK_TYPE_IN.equals(stockType) ? stock.getQuantity() + issueProduct.getQuantity() : stock.getQuantity() - issueProduct.getQuantity());
        // 设置 单价
        stock.setPrice(issueProduct.getPrice());
        // 设置 总成本
        stock.setAmount(Define.STOCK_TYPE_IN.equals(stockType) ? stock.getAmount() + issueProduct.getAmount() : stock.getAmount() - issueProduct.getAmount());

        // 新增/更新 库存商品 wc_stock
        saveOrUpdate(stock); // == this.saveOrUpdate(stock);

        // ---
        // 新增 出入库记录
        StockRecord record = new StockRecord();
        record.setIssueDate(issueProduct.getIssueDate());
        record.setBusinessType(issueProduct.getBusinessType());
        record.setBusinessId(issueProduct.getBusinessId());
        record.setProductId(issueProduct.getProductId());
        record.setWarehouseId(issueProduct.getWarehouseId());
        record.setQuantity(issueProduct.getQuantity());
        record.setStockType(stockType);
        record.setCurrentQuantity(stock.getQuantity());
        record.setPrice(issueProduct.getPrice());
        record.setAmount(issueProduct.getAmount());
        // 新增 wc_stock_record
        recordService.save(record);

        return stock;
    }

    @Override
    public void rollbackStock(String businessId) {
        List<StockRecord> stockRecordList = stockRecordService.findListByBusiness(businessId);

        for (StockRecord stockRecord : stockRecordList) {
            Stock stock = findByProductAndWarehouse(stockRecord.getProductId(), stockRecord.getWarehouseId());

            if (stock == null) {
                continue;
            }

            // 设置 数量，写法1
            if (Define.STOCK_TYPE_IN.equals(stockRecord.getStockType())) {
                stock.setQuantity(stock.getQuantity() - stockRecord.getQuantity());
            } else {
                stock.setQuantity(stock.getQuantity() + stockRecord.getQuantity());
            }
            // 设置 数量，写法2
            // stock.setQuantity(Define.STOCK_TYPE_IN.equals(stockRecord.getStockType()) ? stock.getQuantity() + stockRecord.getQuantity() : stock.getQuantity() - stockRecord.getQuantity());

            // 设置 总成本，写法1
            if (Define.STOCK_TYPE_IN.equals(stockRecord.getStockType())) {
                stock.setAmount(stock.getAmount() - stockRecord.getAmount());
            } else {
                stock.setAmount(stock.getAmount() + stockRecord.getAmount());
            }
            // 设置 总成本，写法2
            // stock.setAmount(Define.STOCK_TYPE_IN.equals(stockRecord.getStockType()) ? stock.getAmount() + stockRecord.getAmount() : stock.getAmount() - stockRecord.getAmount());

            // 新增/更新 库存商品 wc_stock
            // saveOrUpdate(stock);
            // 更新 库存商品 wc_stock
            updateById(stock);
        }
    }

    @Override
    public void handleTransferStock(TransferProduct transferProduct) {
        Product product = productService.getById(transferProduct.getProductId());

        Stock fromStock = findByProductAndWarehouse(transferProduct.getProductId(), transferProduct.getFromWarehouseId());
        if (fromStock == null) {
            fromStock = new Stock();
            fromStock.setProductId(transferProduct.getProductId());
            fromStock.setWarehouseId(transferProduct.getFromWarehouseId());
            // 设置 调出仓库 的单价：使用商品的 “预计采购价” 作为库存成本对外展示的 “单价”，而非 “售价” or “批发价”。
            fromStock.setPrice(product.getEstimatedPurchasePrice());
            // fromStock.setPrice(0.0);
        }

        Stock toStock = findByProductAndWarehouse(transferProduct.getProductId(), transferProduct.getToWarehouseId());
        if (toStock == null) {
            toStock = new Stock();
            toStock.setProductId(transferProduct.getProductId());
            toStock.setWarehouseId(transferProduct.getToWarehouseId());
            // 设置 调入仓库 的单价：使用商品的 “预计采购价” 作为库存成本对外展示的 “单价”，而非 “售价” or “批发价”。
            toStock.setPrice(product.getEstimatedPurchasePrice());
            // toStock.setPrice(0.0);
        }

        // 转移成本
        Double productAmount = product.getEstimatedPurchasePrice() * transferProduct.getQuantity();

        // 设置 调出仓库 的数量
        fromStock.setQuantity(fromStock.getQuantity() - transferProduct.getQuantity());
        // 设置 调出仓库 的总成本
        fromStock.setAmount(fromStock.getAmount() - productAmount);
        // 新增/更新 库存商品 wc_stock
        saveOrUpdate(fromStock); // == this.saveOrUpdate(fromStock);

        // 设置 调入仓库 的数量
        toStock.setQuantity(toStock.getQuantity() + transferProduct.getQuantity());
        // 设置 调入仓库 的总成本
        toStock.setAmount(toStock.getAmount() + productAmount);
        // 新增/更新 库存商品 wc_stock
        saveOrUpdate(toStock); // == this.saveOrUpdate(toStock);


        // ---
        // 新增 出入库记录
        StockRecord fromRecord = new StockRecord();
        fromRecord.setIssueDate(transferProduct.getIssueDate());
        fromRecord.setBusinessType(Define.BUSINESS_TYPE_TRANSFER_OUT);
        fromRecord.setBusinessId(transferProduct.getTransferId());
        fromRecord.setProductId(transferProduct.getProductId());
        fromRecord.setWarehouseId(transferProduct.getFromWarehouseId());
        fromRecord.setQuantity(transferProduct.getQuantity());
        fromRecord.setStockType(Define.STOCK_TYPE_OUT);
        fromRecord.setCurrentQuantity(fromStock.getQuantity());
        fromRecord.setPrice(fromStock.getPrice());
        fromRecord.setAmount(productAmount);
        // 新增 wc_stock_record
        recordService.save(fromRecord);

        StockRecord toRecord = new StockRecord();
        toRecord.setIssueDate(transferProduct.getIssueDate());
        toRecord.setBusinessType(Define.BUSINESS_TYPE_TRANSFER_IN);
        toRecord.setBusinessId(transferProduct.getTransferId());
        toRecord.setProductId(transferProduct.getProductId());
        toRecord.setWarehouseId(transferProduct.getToWarehouseId());
        toRecord.setQuantity(transferProduct.getQuantity());
        toRecord.setStockType(Define.STOCK_TYPE_IN);
        toRecord.setCurrentQuantity(toRecord.getQuantity());
        toRecord.setPrice(fromStock.getPrice());
        toRecord.setAmount(productAmount);
        // 新增 wc_stock_record
        recordService.save(toRecord);
    }

    @Override
    public void rollbackTransferStock(String businessId) {
        List<StockRecord> transferStockRecordList = stockRecordService.findListByBusiness(businessId);

        for (StockRecord transferStockRecord : transferStockRecordList) {
            Stock transferStock = findByProductAndWarehouse(transferStockRecord.getProductId(), transferStockRecord.getWarehouseId());

            if (transferStock == null) {
                continue;
            }

            // 设置 数量
            if (Define.STOCK_TYPE_IN.equals(transferStockRecord.getStockType())) {
                transferStock.setQuantity(transferStock.getQuantity() - transferStockRecord.getQuantity());
            } else {
                transferStock.setQuantity(transferStock.getQuantity() + transferStockRecord.getQuantity());
            }

            // 设置 总成本
            if (Define.STOCK_TYPE_IN.equals(transferStockRecord.getStockType())) {
                transferStock.setAmount(transferStock.getAmount() - transferStockRecord.getAmount());
            } else {
                transferStock.setAmount(transferStock.getAmount() + transferStockRecord.getAmount());
            }

            // 新增/更新 库存商品 wc_stock
            // saveOrUpdate(transferStock);
            // 更新 库存商品 wc_stock
            updateById(transferStock);
        }
    }

}
