<template>
  <div>
    <!-- 面包屑导航区域 -->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>购货管理</el-breadcrumb-item>
      <el-breadcrumb-item>购货单新增</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 卡片视图区域 -->
    <el-card>
      <el-form :model="saveForm"
               :rules="saveFormRules"
               ref="saveFormRef"
               label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="供应商"
                          prop="supplierId">
              <el-select v-model="saveForm.supplierId"
                         placeholder="请选择供应商">
                <el-option v-for="supplier in supplierList"
                           :key="supplier.id"
                           :label="supplier.name"
                           :value="supplier.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单据日期"
                          prop="issueDate">
              <el-date-picker v-model="saveForm.issueDate"
                              type="date"
                              value-format='yyyy-MM-dd'
                              placeholder="选择日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单据编号"
                          prop="code">
              <el-input v-model="saveForm.code"
                        disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <!-- 商品表格 -->
          <el-card>
            <!-- 搜索与添加区域 -->
            <el-row :gutter="20">
              <el-col :span="4">
                <el-button type="primary"
                           @click="showProductAddDialog()">新增商品</el-button>
              </el-col>
            </el-row>
            <!-- 商品列表区域 -->
            <el-table :data="saveForm.productList"
                      border
                      stripe
                      height="300">
              <el-table-column label="商品"
                               prop="productName"></el-table-column>
              <el-table-column label="单位"
                               prop="unitName"></el-table-column>
              <el-table-column label="仓库"
                               prop="warehouseName"></el-table-column>
              <el-table-column label="数量"
                               prop="quantity"></el-table-column>
              <el-table-column label="购货单价"
                               prop="price"></el-table-column>
              <el-table-column label="折扣率(%)"
                               prop="discountRate"></el-table-column>
              <el-table-column label="折扣额"
                               prop="discountAmount"></el-table-column>
              <el-table-column label="购货金额"
                               prop="amount"></el-table-column>
              <el-table-column label="序列号"
                               prop="code"></el-table-column>
              <el-table-column label="备注"
                               prop="remark"></el-table-column>
              <el-table-column label="操作"
                               width="120px">
                <template slot-scope="scope">
                  <!-- 修改按钮 -->
                  <el-tooltip effect="dark"
                              content="修改"
                              placement="top"
                              :enterable="false">
                    <el-button type="primary"
                               icon="el-icon-edit"
                               size="mini"
                               @click="showProductEditDialog(scope.row, scope.$index)"></el-button>
                  </el-tooltip>
                  <!-- 删除按钮 -->
                  <el-tooltip effect="dark"
                              content="删除"
                              placement="top"
                              :enterable="false">
                    <el-button type="danger"
                               icon="el-icon-delete"
                               size="mini"
                               @click="deleteProduct(scope.row)"></el-button>
                  </el-tooltip>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-row>
        <el-divider content-position="left">结算信息</el-divider>
        <el-row>
          <el-col :span="5">
            <el-form-item label="优惠率"
                          prop="preferentialRate">
              <el-input v-model.number="saveForm.preferentialRate"
                        @input="onPreferentialRateInput">
                <template slot="append">%</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="优惠金额"
                          prop="preferentialAmount">
              <el-input v-model="saveForm.preferentialAmount"
                        @input="onPreferentialAmountInput"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="优惠后金额"
                          prop="preferredAmount">
              <el-input v-model="saveForm.preferredAmount"
                        @input="onPreferredAmountInput"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="本次付款"
                          prop="currentAmount">
              <el-input v-model="saveForm.currentAmount"
                        @input="onCurrentAmountInput"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="5">
            <el-form-item label="结算账户"
                          prop="accountId">
              <el-select v-model="saveForm.accountId"
                         placeholder="请选择结算账户"
                         @change="selectAccountChanged">
                <el-option v-for="account in accountList"
                           :key="account.id"
                           :label="account.name"
                           :value="account.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="本次欠款"
                          prop="debtAmount">
              <el-input v-model="saveForm.debtAmount"
                        @input="onDebtAmountInput"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="采购合同"
                          prop="contracts">
              <el-input v-model="saveForm.contracts"
                        disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="5">
            <el-form-item label="制单人"
                          prop="listerName">
              <el-input v-model="saveForm.listerName"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <el-row>
        <el-col :offset="20">
          <el-button @click="savePurchase">保存</el-button>
          <el-button type="primary"
                     @click="savePurchaseThenNew">保存并新增</el-button>
        </el-col>

      </el-row>
    </el-card>

    <!-- 保存商品对话框 -->
    <el-dialog :title="isProductAdd ? '新增商品' : '修改商品'"
               :visible.sync="saveProductDialogVisible"
               width="70%"
               @close="saveProductDialogClosed">
      <!-- 内容主体区域 -->
      <el-form :model="saveProductForm"
               :rules="saveProductFormRules"
               ref="saveProductFormRef"
               label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="商品"
                          prop="productName">
              <el-input placeholder="请选择商品"
                        v-model="saveProductForm.productName"
                        disabled>
                <el-button slot="append"
                           icon="el-icon-search"
                           @click="showSelectProductDialog()"></el-button>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单位"
                          prop="unitName">
              <el-input v-model="saveProductForm.unitName"
                        disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="仓库"
                          prop="warehouseId">
              <el-select v-model="saveProductForm.warehouseId"
                         placeholder="请选择仓库"
                         @change="selectWarehouseChanged">
                <el-option v-for="warehouse in warehouseList"
                           :key="warehouse.id"
                           :label="warehouse.name"
                           :value="warehouse.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="数量"
                          prop="quantity">
              <el-input v-model="saveProductForm.quantity"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="购货单价"
                          prop="price">
              <el-input v-model="saveProductForm.price"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="折扣率(%)"
                          prop="discountRate">
              <el-input v-model="saveProductForm.discountRate"
                        @input="onDiscountRateInput"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="折扣额"
                          prop="discountAmount">
              <el-input v-model="saveProductForm.discountAmount"
                        @input="onDiscountAmountInput"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="购货金额"
                          prop="amount">
              <el-input v-model="saveProductForm.amount"
                        disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="序列号"
                          prop="code">
              <el-input v-model="saveProductForm.code"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-form-item label="备注信息"
                          prop="remark">
              <el-input type="textarea"
                        :rows="2"
                        placeholder="添加备注信息"
                        v-model="saveProductForm.remark">
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <!-- 底部区域 -->
      <span slot="footer"
            class="dialog-footer">
        <el-button @click="saveProductDialogVisible = false">取 消</el-button>
        <el-button type="primary"
                   @click="saveProduct">确 定</el-button>
      </span>
    </el-dialog>

    <select-product-dialog :visible.sync="selectProductDialogVisible"
                           @selectProduct="handleSelectProduct($event)"></select-product-dialog>
  </div>
</template>

<script>
import Vue from 'vue'
import SelectProductDialog from '../common/SelectProductDialog'
Vue.component('select-product-dialog', SelectProductDialog)

export default {
  watch: {
    // 数量/单价变更时，按当前编辑模式自动重算
    'saveProductForm.quantity': 'handleQuantityOrPriceChanged',
    'saveProductForm.price': 'handleQuantityOrPriceChanged',
    // 折扣率和折扣额是双向联动，分别走不同计算分支
    'saveProductForm.discountRate': 'handleDiscountRateChanged',
    'saveProductForm.discountAmount': 'handleDiscountAmountChanged',
    // 结算信息联动：优惠三字段双向换算 + 付款/欠款双向换算
    'saveForm.preferentialRate': 'handlePreferentialRateChanged',
    'saveForm.preferentialAmount': 'handlePreferentialAmountChanged',
    'saveForm.preferredAmount': 'handlePreferredAmountChanged',
    'saveForm.currentAmount': 'handleCurrentAmountChanged',
    'saveForm.debtAmount': 'handleDebtAmountChanged',
    'saveForm.productList': {
      handler: 'handleProductListChanged',
      deep: true
    }
  },
  data() {
    return {
      // 供应商
      supplierList: [],
      purchaseCode: '',
      accountList: [],
      saveForm: {
        type: 'buy',
        preferentialRate: 0,
        preferentialAmount: 0,
        preferredAmount: 0.0,
        currentAmount: 0.0,
        debtAmount: 0.0,
        productList: [],
        accountList: []
      },
      saveFormRules: {},
      // 是否新增商品
      isProductAdd: true,
      modifyProductIndex: 0,
      saveProductDialogVisible: false,
      saveProductForm: {
        quantity: 1.0,
        price: 0.0,
        discountRate: 0,
        discountAmount: 0.0,
        amount: 0.0
      },
      // 添加表单的验证规则对象
      saveProductFormRules: {
        productName: [{ required: true, message: '请选择商品', trigger: 'blur' }],
        warehouseId: [{ required: true, message: '请选择仓库', trigger: 'blur' }]
      },
      selectProductDialogVisible: false,
      productParams: {
        query: {
          categoryId: ''
        },
        current: 1,
        size: 5
      },
      warehouseList: [],
      // rate: 以折扣率为主输入；amount: 以折扣额为主输入
      discountCalcMode: 'rate',
      // 同步联动字段时的保护锁，避免 watcher 循环触发
      isDiscountSyncing: false,
      // rate: 以优惠率为主输入；amount: 以优惠金额为主输入；preferred: 以优惠后金额为主输入
      settlementDiscountCalcMode: 'rate',
      // current: 以本次付款为主输入；debt: 以本次欠款为主输入
      paymentCalcMode: 'current',
      isSettlementSyncing: false,
      isPaymentSyncing: false
    }
  },
  created() {
    let purchaseId = this.$route.query.purchaseId
    if (purchaseId !== undefined) {
      console.log(purchaseId)
      this.getPurchaseDetail(purchaseId)
    } else {
      this.getPurchaseCode()
    }

    this.getSupplierList()
    this.getAccountList()
    this.getWarehouseList()
  },
  methods: {
    /**
     * 获取供应商列表
     * 从后端API获取所有供应商信息，用于购货单中的供应商选择下拉框
     */
    async getSupplierList() {
      const { data: result } = await this.$http.post('/supplier/page', {
        current: 1,
        size: 10000
      })
      if (!result.success) return this.$message.error(result.message)

      this.supplierList = result.data.supplierPage.records
    },
    /**
     * 获取购货单单据编号
     * 调用后端API生成新的购货单编号，并重置表单数据为初始状态
     */
    async getPurchaseCode() {
      const { data: result } = await this.$http.post('/purchase/createCode')
      if (!result.success) return this.$message.error(result.message)

      this.saveForm = {
        id: '',
        type: 'buy',
        preferentialRate: 0,
        preferentialAmount: 0,
        preferredAmount: 0.0,
        currentAmount: 0.0,
        debtAmount: 0.0,
        productList: [],
        accountList: []
      }

      console.log(result.data.code)
      this.purchaseCode = result.data.code
      this.saveForm.code = this.purchaseCode
      this.recalculateSettlementByMode()
    },
    /**
     * 获取结算账户列表
     * 从后端API获取所有可用的结算账户，用于购货单中的结算账户选择
     */
    async getAccountList() {
      const { data: result } = await this.$http.post('/settlementAccount/list')
      if (!result.success) return this.$message.error(result.message)

      this.accountList = result.data.accountList
    },
    /**
     * 获取购货单详情
     * 根据购货单ID从后端获取详细信息，用于编辑已有购货单
     * @param {string} purchaseId - 购货单ID
     */
    async getPurchaseDetail(purchaseId) {
      const { data: result } = await this.$http.post('/purchase/detail', {
        purchaseId
      })
      if (!result.success) return this.$message.error(result.message)

      console.log(result.data)
      this.saveForm = result.data.purchase
      this.recalculateSettlementByMode()
    },
    /**
     * 结算账户变更处理
     * 当用户选择结算账户时，将选中的账户ID添加到购货单的账户列表中
     * @param {string} accountId - 选中的结算账户ID
     */
    selectAccountChanged(accountId) {
      this.saveForm.accountList = []
      var account = {
        accountId
      }
      this.saveForm.accountList.push(account)
    },
    /**
     * 保存购货单
     * 验证表单后，将购货单数据提交到后端进行保存
     */
    async savePurchase() {
      this.$refs.saveFormRef.validate(async (valid) => {
        if (!valid) return
        // 可以发起新增购货单的网络请求
        const { data: result } = await this.$http.post('/purchase/save', {
          purchase: this.saveForm,
          productList: this.saveForm.productList,
          accountList: this.saveForm.accountList
        })
        if (!result.success) {
          return this.$message.error(result.message)
        }

        this.$message.success('保存购货单成功！')
        console.log(result)
        this.saveForm.id = result.data.purchase.id
      })
    },
    /**
     * 保存购货单并创建新单
     * 先保存当前购货单，然后生成新的单据编号，准备创建下一个购货单
     */
    async savePurchaseThenNew() {
      this.savePurchase()
      this.getPurchaseCode()
    },
    /**
     * 显示新增商品对话框
     * 初始化商品表单为默认值，设置模式为新增，打开商品编辑对话框
     */
    showProductAddDialog() {
      this.isProductAdd = true
      this.discountCalcMode = 'rate'
      this.saveProductForm = {
        quantity: 1.0,
        price: 0.0,
        discountRate: 0,
        discountAmount: 0.0,
        amount: 0.0
      }
      this.saveProductDialogVisible = true
    },
    /**
     * 显示修改商品对话框
     * 加载指定商品的详细信息到编辑表单，设置模式为修改，打开商品编辑对话框
     * @param {object} contact - 要修改的商品对象
     * @param {number} index - 商品在列表中的索引位置
     */
    showProductEditDialog(contact, index) {
      this.isProductAdd = false
      this.discountCalcMode = 'rate'
      this.saveProductForm = this.$_.cloneDeep(contact)
      this.modifyProductIndex = index
      this.saveProductDialogVisible = true
    },

    /**
     * 商品对话框关闭后的清理工作
     * 如果是新增模式，重置表单字段；修改模式不需要重置
     */
    saveProductDialogClosed() {
      if (this.isProductAdd) {
        this.$refs.saveProductFormRef.resetFields()
      }
    },
    /**
     * 保存商品信息
     * 验证商品表单后，根据模式执行新增或修改操作，并重新计算结算信息
     */
    saveProduct() {
      this.$refs.saveProductFormRef.validate(async (valid) => {
        if (!valid) return

        var product = this.$_.cloneDeep(this.saveProductForm)

        // 修改
        if (!this.isProductAdd) {
          this.saveForm.productList.splice(this.modifyProductIndex, 1, product)

          this.$message.success('修改商品成功！')
        } else {
          // 新增
          this.saveForm.productList.push(product)
          this.$message.success('添加商品成功！')
        }
        this.recalculateSettlementByMode()

        // 隐藏添加商品的对话框
        this.saveProductDialogVisible = false
      })
    },
    /**
     * 删除商品
     * 弹出确认对话框，用户确认后从商品列表中移除指定商品，并重新计算结算信息
     * @param {object} product - 要删除的商品对象
     */
    async deleteProduct(product) {
      // 弹框询问用户是否删除数据
      const confirmResult = await this.$confirm('删除该商品，是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).catch((err) => err)

      // 如果用户确认删除，则返回值为字符串 confirm
      // 如果用户取消了删除，则返回值为字符串 cancel
      if (confirmResult !== 'confirm') {
        return this.$message.info('已取消删除')
      }

      this.saveForm.productList.splice(
        this.saveForm.productList.findIndex((existedProduct) => existedProduct === product),
        1
      )
      this.recalculateSettlementByMode()

      this.$message.success('删除商品成功！')
    },
    /**
     * 显示选择商品对话框
     * 打开商品选择器对话框，让用户从商品库中选择商品
     */
    showSelectProductDialog() {
      this.selectProductDialogVisible = true
    },
    /**
     * 处理商品选择结果
     * 当用户在商品选择器中选定商品后，填充商品基本信息到编辑表单
     * @param {object} product - 用户选择的商品对象
     */
    handleSelectProduct(product) {
      if (product != null) {
        this.saveProductForm.productName = product.name
        this.saveProductForm.productId = product.id
        this.saveProductForm.unitName = product.unitName
        this.saveProductForm.price = this.getProductPrice(product)
        this.saveProductForm.discountRate = this.getProductDiscountRate(product)
        this.discountCalcMode = 'rate'
        this.calculateByDiscountRate()
      }
    },
    /**
     * 折扣率输入处理
     * 标记当前计算模式为按折扣率计算，用于后续自动计算逻辑判断
     */
    onDiscountRateInput() {
      // 用户正在改折扣率，后续计算优先由折扣率驱动
      this.discountCalcMode = 'rate'
    },
    /**
     * 折扣额输入处理
     * 标记当前计算模式为按折扣额计算，用于后续自动计算逻辑判断
     */
    onDiscountAmountInput() {
      // 用户正在改折扣额，后续计算优先由折扣额驱动
      this.discountCalcMode = 'amount'
    },
    /**
     * 数量或单价变更处理
     * 当商品数量或单价发生变化时，根据当前计算模式重新计算折扣和金额
     */
    handleQuantityOrPriceChanged() {
      this.recalculateByMode()
    },
    /**
     * 折扣率变更处理
     * 当折扣率发生变化且处于同步保护状态外时，按折扣率重新计算相关字段
     */
    handleDiscountRateChanged() {
      if (this.isDiscountSyncing || this.discountCalcMode !== 'rate') {
        return
      }
      this.calculateByDiscountRate()
    },
    /**
     * 折扣额变更处理
     * 当折扣额发生变化且处于同步保护状态外时，按折扣额重新计算相关字段
     */
    handleDiscountAmountChanged() {
      if (this.isDiscountSyncing || this.discountCalcMode !== 'amount') {
        return
      }
      this.calculateByDiscountAmount()
    },
    /**
     * 根据当前模式重新计算商品折扣和金额
     * 根据discountCalcMode决定是按折扣率还是折扣额进行计算
     */
    recalculateByMode() {
      if (this.discountCalcMode === 'amount') {
        this.calculateByDiscountAmount()
      } else {
        this.calculateByDiscountRate()
      }
    },
    /**
     * 按折扣率计算商品金额
     * 公式：折扣额 = 数量 × 单价 × 折扣率；购货金额 = 小计 - 折扣额
     */
    calculateByDiscountRate() {
      const quantity = this.toNumber(this.saveProductForm.quantity)
      const price = this.toNumber(this.saveProductForm.price)
      const subtotal = quantity * price
      const discountRate = this.toNumber(this.saveProductForm.discountRate)
      const discountAmount = this.rateToDiscountAmount(subtotal, discountRate)
      const amount = subtotal - discountAmount
      this.syncDiscountFields({
        discountAmount,
        amount
      })
    },
    /**
     * 按折扣额计算商品金额和折扣率
     * 公式：折扣率 = 折扣额 / (数量 × 单价)；购货金额 = 小计 - 折扣额
     */
    calculateByDiscountAmount() {
      const quantity = this.toNumber(this.saveProductForm.quantity)
      const price = this.toNumber(this.saveProductForm.price)
      const subtotal = quantity * price
      const rawDiscountAmount = this.toNumber(this.saveProductForm.discountAmount)
      const discountAmount = this.limitDiscountAmount(subtotal, rawDiscountAmount)
      const discountRate = this.discountAmountToRate(subtotal, discountAmount, this.saveProductForm.discountRate)
      const amount = subtotal - discountAmount
      this.syncDiscountFields({
        discountRate,
        discountAmount,
        amount
      })
    },
    /**
     * 同步折扣相关字段
     * 更新折扣率、折扣额和购货金额，使用同步锁防止watcher循环触发
     * @param {object} params - 包含discountRate、discountAmount、amount的对象
     */
    syncDiscountFields({ discountRate, discountAmount, amount }) {
      this.isDiscountSyncing = true
      if (discountRate !== undefined) {
        this.saveProductForm.discountRate = this.toFixedNumber(discountRate)
      }
      if (discountAmount !== undefined) {
        this.saveProductForm.discountAmount = this.toFixedNumber(discountAmount)
      }
      if (amount !== undefined) {
        this.saveProductForm.amount = this.toFixedNumber(amount > 0 ? amount : 0)
      }
      this.$nextTick(() => {
        this.isDiscountSyncing = false
      })
    },
    /**
     * 将折扣率转换为折扣额
     * 支持两种输入格式：0~1的小数形式和0~100的百分比形式
     * @param {number} subtotal - 小计金额
     * @param {number} discountRate - 折扣率
     * @returns {number} 折扣额
     */
    rateToDiscountAmount(subtotal, discountRate) {
      if (subtotal <= 0) {
        return 0
      }
      const normalizedRate = this.toNumber(discountRate)
      if (normalizedRate <= 0) {
        return 0
      }
      return normalizedRate <= 1 ? subtotal * normalizedRate : subtotal * (normalizedRate / 100)
    },
    /**
     * 将折扣额转换为折扣率
     * 根据当前折扣率的输入习惯保持输出格式一致性
     * @param {number} subtotal - 小计金额
     * @param {number} discountAmount - 折扣额
     * @param {number} currentRate - 当前折扣率值
     * @returns {number} 折扣率
     */
    discountAmountToRate(subtotal, discountAmount, currentRate) {
      if (subtotal <= 0) {
        return 0
      }
      const rateByRatio = discountAmount / subtotal
      // 保持当前折扣率输入习惯：若此前按百分比输入，则继续输出百分比
      return this.toNumber(currentRate) > 1 ? rateByRatio * 100 : rateByRatio
    },
    /**
     * 限制折扣额范围
     * 确保折扣额在[0, 小计]范围内，防止出现负金额
     * @param {number} subtotal - 小计金额
     * @param {number} discountAmount - 原始折扣额
     * @returns {number} 限制后的折扣额
     */
    limitDiscountAmount(subtotal, discountAmount) {
      if (subtotal <= 0) {
        return 0
      }
      if (discountAmount <= 0) {
        return 0
      }
      return discountAmount > subtotal ? subtotal : discountAmount
    },
    /**
     * 获取商品价格
     * 按优先级获取商品价格：预估采购价 > 批发价 > 普通价格
     * @param {object} product - 商品对象
     * @returns {number} 格式化后的价格
     */
    getProductPrice(product) {
      return this.toFixedNumber(
        this.toNumber(product.estimatedPurchasePrice || product.wholesalePrice || product.price)
      )
    },
    /**
     * 获取商品折扣率
     * 按优先级获取商品折扣率：discountRate1 > discountRate2 > discountRate
     * @param {object} product - 商品对象
     * @returns {number} 格式化后的折扣率
     */
    getProductDiscountRate(product) {
      return this.toFixedNumber(this.toNumber(product.discountRate1 || product.discountRate2 || product.discountRate))
    },
    /**
     * 优惠率输入处理
     * 标记结算优惠计算模式为按优惠率计算
     */
    onPreferentialRateInput() {
      this.settlementDiscountCalcMode = 'rate'
    },
    /**
     * 优惠金额输入处理
     * 标记结算优惠计算模式为按优惠金额计算
     */
    onPreferentialAmountInput() {
      this.settlementDiscountCalcMode = 'amount'
    },
    /**
     * 优惠后金额输入处理
     * 标记结算优惠计算模式为按优惠后金额计算
     */
    onPreferredAmountInput() {
      this.settlementDiscountCalcMode = 'preferred'
    },
    /**
     * 本次付款输入处理
     * 标记付款计算模式为按本次付款计算
     */
    onCurrentAmountInput() {
      this.paymentCalcMode = 'current'
    },
    /**
     * 本次欠款输入处理
     * 标记付款计算模式为按本次欠款计算
     */
    onDebtAmountInput() {
      this.paymentCalcMode = 'debt'
    },
    /**
     * 商品列表变更处理
     * 当购货单中的商品列表发生变化时，重新计算结算信息
     */
    handleProductListChanged() {
      this.recalculateSettlementByMode()
    },
    /**
     * 优惠率变更处理
     * 当优惠率发生变化且处于同步保护状态外时，按优惠率重新计算结算信息
     */
    handlePreferentialRateChanged() {
      if (this.isSettlementSyncing || this.settlementDiscountCalcMode !== 'rate') {
        return
      }
      this.calculateSettlementByRate()
    },
    /**
     * 优惠金额变更处理
     * 当优惠金额发生变化且处于同步保护状态外时，按优惠金额重新计算结算信息
     */
    handlePreferentialAmountChanged() {
      if (this.isSettlementSyncing || this.settlementDiscountCalcMode !== 'amount') {
        return
      }
      this.calculateSettlementByAmount()
    },
    /**
     * 优惠后金额变更处理
     * 当优惠后金额发生变化时，先重新计算付款信息，再按优惠后金额重新计算结算信息
     */
    handlePreferredAmountChanged() {
      if (!this.isPaymentSyncing) {
        this.recalculatePaymentByMode()
      }
      if (this.isSettlementSyncing || this.settlementDiscountCalcMode !== 'preferred') {
        return
      }
      this.calculateSettlementByPreferredAmount()
    },
    /**
     * 本次付款变更处理
     * 当本次付款发生变化且处于同步保护状态外时，按本次付款重新计算欠款
     */
    handleCurrentAmountChanged() {
      if (this.isPaymentSyncing || this.paymentCalcMode !== 'current') {
        return
      }
      this.calculateByCurrentAmount()
    },
    /**
     * 本次欠款变更处理
     * 当本次欠款发生变化且处于同步保护状态外时，按本次欠款重新计算付款
     */
    handleDebtAmountChanged() {
      if (this.isPaymentSyncing || this.paymentCalcMode !== 'debt') {
        return
      }
      this.calculateByDebtAmount()
    },
    /**
     * 根据当前模式重新计算结算信息
     * 根据settlementDiscountCalcMode决定按哪种方式计算优惠信息
     */
    recalculateSettlementByMode() {
      if (this.settlementDiscountCalcMode === 'amount') {
        this.calculateSettlementByAmount()
      } else if (this.settlementDiscountCalcMode === 'preferred') {
        this.calculateSettlementByPreferredAmount()
      } else {
        this.calculateSettlementByRate()
      }
    },
    /**
     * 根据当前模式重新计算付款信息
     * 根据paymentCalcMode决定按本次付款还是本次欠款进行计算
     */
    recalculatePaymentByMode() {
      if (this.paymentCalcMode === 'debt') {
        this.calculateByDebtAmount()
      } else {
        this.calculateByCurrentAmount()
      }
    },
    /**
     * 按优惠率计算结算信息
     * 公式：优惠金额 = 商品总金额 × 优惠率；优惠后金额 = 商品总金额 - 优惠金额
     */
    calculateSettlementByRate() {
      const totalAmount = this.getProductTotalAmount()
      const preferentialRate = this.toNumber(this.saveForm.preferentialRate)
      const preferentialAmount = this.rateToDiscountAmount(totalAmount, preferentialRate)
      const preferredAmount = totalAmount - preferentialAmount
      this.syncSettlementFields({
        preferentialAmount,
        preferredAmount
      })
    },
    /**
     * 按优惠金额计算结算信息
     * 公式：优惠率 = 优惠金额 / 商品总金额；优惠后金额 = 商品总金额 - 优惠金额
     */
    calculateSettlementByAmount() {
      const totalAmount = this.getProductTotalAmount()
      const rawPreferentialAmount = this.toNumber(this.saveForm.preferentialAmount)
      const preferentialAmount = this.limitDiscountAmount(totalAmount, rawPreferentialAmount)
      const preferentialRate = this.discountAmountToRate(
        totalAmount,
        preferentialAmount,
        this.saveForm.preferentialRate
      )
      const preferredAmount = totalAmount - preferentialAmount
      this.syncSettlementFields({
        preferentialRate,
        preferentialAmount,
        preferredAmount
      })
    },
    /**
     * 按优惠后金额计算结算信息
     * 公式：优惠金额 = 商品总金额 - 优惠后金额；优惠率 = 优惠金额 / 商品总金额
     */
    calculateSettlementByPreferredAmount() {
      const totalAmount = this.getProductTotalAmount()
      const rawPreferredAmount = this.toNumber(this.saveForm.preferredAmount)
      const preferredAmount = this.limitPreferredAmount(totalAmount, rawPreferredAmount)
      const preferentialAmount = totalAmount - preferredAmount
      const preferentialRate = this.discountAmountToRate(
        totalAmount,
        preferentialAmount,
        this.saveForm.preferentialRate
      )
      this.syncSettlementFields({
        preferentialRate,
        preferentialAmount,
        preferredAmount
      })
    },
    /**
     * 同步结算相关字段
     * 更新优惠率、优惠金额和优惠后金额，使用同步锁防止watcher循环触发
     * @param {object} params - 包含preferentialRate、preferentialAmount、preferredAmount的对象
     */
    syncSettlementFields({ preferentialRate, preferentialAmount, preferredAmount }) {
      this.isSettlementSyncing = true
      if (preferentialRate !== undefined) {
        this.saveForm.preferentialRate = this.toFixedNumber(preferentialRate)
      }
      if (preferentialAmount !== undefined) {
        this.saveForm.preferentialAmount = this.toFixedNumber(preferentialAmount)
      }
      if (preferredAmount !== undefined) {
        this.saveForm.preferredAmount = this.toFixedNumber(preferredAmount)
      }
      this.$nextTick(() => {
        this.isSettlementSyncing = false
        this.recalculatePaymentByMode()
      })
    },
    /**
     * 按本次付款计算欠款
     * 公式：本次欠款 = 优惠后金额 - 本次付款
     */
    calculateByCurrentAmount() {
      const preferredAmount = this.toNumber(this.saveForm.preferredAmount)
      const currentAmount = this.limitAmountByTotal(preferredAmount, this.saveForm.currentAmount)
      const debtAmount = preferredAmount - currentAmount
      this.syncPaymentFields({
        currentAmount,
        debtAmount
      })
    },
    /**
     * 按本次欠款计算付款
     * 公式：本次付款 = 优惠后金额 - 本次欠款
     */
    calculateByDebtAmount() {
      const preferredAmount = this.toNumber(this.saveForm.preferredAmount)
      const debtAmount = this.limitAmountByTotal(preferredAmount, this.saveForm.debtAmount)
      const currentAmount = preferredAmount - debtAmount
      this.syncPaymentFields({
        currentAmount,
        debtAmount
      })
    },
    /**
     * 同步付款相关字段
     * 更新本次付款和本次欠款，使用同步锁防止watcher循环触发
     * @param {object} params - 包含currentAmount、debtAmount的对象
     */
    syncPaymentFields({ currentAmount, debtAmount }) {
      this.isPaymentSyncing = true
      if (currentAmount !== undefined) {
        this.saveForm.currentAmount = this.toFixedNumber(currentAmount)
      }
      if (debtAmount !== undefined) {
        this.saveForm.debtAmount = this.toFixedNumber(debtAmount)
      }
      this.$nextTick(() => {
        this.isPaymentSyncing = false
      })
    },
    /**
     * 计算商品总金额
     * 遍历商品列表，累加所有商品的购货金额
     * @returns {number} 格式化后的商品总金额
     */
    getProductTotalAmount() {
      return this.toFixedNumber(
        this.saveForm.productList.reduce(
          (sum, product) => sum + this.toNumber(product.amount),
          0
        )
      )
    },
    /**
     * 限制优惠后金额范围
     * 确保优惠后金额不超过商品总金额
     * @param {number} totalAmount - 商品总金额
     * @param {number} preferredAmount - 原始优惠后金额
     * @returns {number} 限制后的优惠后金额
     */
    limitPreferredAmount(totalAmount, preferredAmount) {
      return this.limitAmountByTotal(totalAmount, preferredAmount)
    },
    /**
     * 限制金额不超过总金额
     * 确保目标金额在合理范围内，不超过总金额且不为负数
     * @param {number} totalAmount - 总金额
     * @param {number} targetAmount - 目标金额
     * @returns {number} 限制后的金额
     */
    limitAmountByTotal(totalAmount, targetAmount) {
      const normalizedTotal = this.toNumber(totalAmount)
      const normalizedTarget = this.toNumber(targetAmount)
      if (normalizedTotal <= 0) {
        return 0
      }
      if (normalizedTarget <= 0) {
        return 0
      }
      return normalizedTarget > normalizedTotal ? normalizedTotal : normalizedTarget
    },
    /**
     * 转换为数字类型
     * 将任意值转换为数字，如果转换失败则返回0
     * @param {*} value - 要转换的值
     * @returns {number} 转换后的数字
     */
    toNumber(value) {
      const numberValue = Number(value)
      if (Number.isNaN(numberValue)) {
        return 0
      }
      return numberValue
    },
    /**
     * 转换为保留两位小数的数字
     * 先将值转换为数字，然后格式化为两位小数
     * @param {*} value - 要转换的值
     * @returns {number} 保留两位小数的数字
     */
    toFixedNumber(value) {
      return Number(this.toNumber(value).toFixed(2))
    },
    /**
     * 获取仓库列表
     * 从后端API获取所有仓库信息，用于商品编辑时的仓库选择
     */
    async getWarehouseList() {
      const { data: result } = await this.$http.post('/warehouse/page', {
        current: 1,
        size: 10000
      })
      if (!result.success) return this.$message.error(result.message)

      this.warehouseList = result.data.warehousePage.records
    },
    // 选择了仓库
    selectWarehouseChanged(warehouseId) {
      var warehouse = this.$_.find(this.warehouseList, { id: warehouseId })
      if (warehouse !== undefined) {
        this.saveProductForm.warehouseName = warehouse.name
      }
    }
  }
}
</script>

<style lang="less" scoped>
.query {
  margin-bottom: 20px;
}
.el-select {
  width: 100%;
}
.el-date-editor {
  width: 100%;
}
</style>
