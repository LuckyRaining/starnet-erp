<template>
  <div>
    <!-- 面包屑导航区域 -->
    <el-breadcrumb separator-class="el-icon-arrow-right">
      <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>销货管理</el-breadcrumb-item>
      <el-breadcrumb-item>销货退货单新增</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 卡片视图区域 -->
    <el-card>
      <el-form :model="saveForm"
               :rules="saveFormRules"
               ref="saveFormRef"
               label-width="100px">
        <el-row>
          <el-col :span="6">
            <el-form-item label="客户"
                          prop="customerId">
              <el-select v-model="saveForm.customerId"
                         placeholder="请选择客户"
                         @change="selectCustomerChanged">
                <el-option v-for="customer in customerList"
                           :key="customer.id"
                           :label="customer.name"
                           :value="customer.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="销售人员"
                          prop="sellerId">
              <el-select v-model="saveForm.sellerId"
                         placeholder="请选择销售人员">
                <el-option v-for="seller in sellerList"
                           :key="seller.id"
                           :label="seller.name"
                           :value="seller.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="单据日期"
                          prop="issueDate">
              <el-date-picker v-model="saveForm.issueDate"
                              type="date"
                              value-format='yyyy-MM-dd'
                              placeholder="选择日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="单据编号"
                          prop="code">
              <el-input v-model="saveForm.code"
                        disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="联系人"
                          prop="contactName">
              <el-select v-model="saveForm.contactName"
                         placeholder="请选择联系人"
                         @change="selectContactChanged">
                <el-option v-for="contact in contactList"
                           :key="contact.id"
                           :label="contact.name"
                           :value="contact.name">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="地址"
                          prop="address">
              <el-input v-model="saveForm.address"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="电话"
                          prop="phone">
              <el-input v-model="saveForm.phone"></el-input>
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
              <el-table-column label="销售单价"
                               prop="price"></el-table-column>
              <el-table-column label="折扣率(%)"
                               prop="discountRate"></el-table-column>
              <el-table-column label="折扣额"
                               prop="discountAmount"></el-table-column>
              <el-table-column label="金额"
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
          <el-col :span="6">
            <el-form-item label="优惠率"
                          prop="preferentialRate">
              <el-input v-model.number="saveForm.preferentialRate"
                        @input="onPreferentialRateInput">
                <template slot="append">%</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="优惠金额"
                          prop="preferentialAmount">
              <el-input v-model="saveForm.preferentialAmount"
                        @input="onPreferentialAmountInput"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="优惠后金额"
                          prop="preferredAmount">
              <el-input v-model="saveForm.preferredAmount"
                        @input="onPreferredAmountInput"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="客户承担费用"
                          prop="customerFee">
              <el-input v-model="saveForm.customerFee"
                        @input="onCustomerFeeInput"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="本次退款"
                          prop="currentAmount">
              <el-input v-model="saveForm.currentAmount"
                        @input="onCurrentAmountInput"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="结算账户"
                          prop="accountId">
              <el-select v-model="saveForm.accountId"
                         placeholder="请选择结算账户"
                         @change="selectAccountChanged">
                <el-option v-for="account in settlementAccountList"
                           :key="account.id"
                           :label="account.name"
                           :value="account.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="本次欠款"
                          prop="debtAmount">
              <el-input v-model="saveForm.debtAmount"
                        @input="onDebtAmountInput"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="6">
            <el-form-item label="销售附件"
                          prop="attachments">
              <el-input v-model="saveForm.attachments"
                        disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="5">
            <el-form-item label="制单人"
                          prop="listerId">
              <el-select v-model="saveForm.listerId"
                         placeholder="请选择制单人"
                         filterable
                         clearable>
                <el-option v-for="user in userList"
                           :key="user.id"
                           :label="userDisplayName(user)"
                           :value="user.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="审核人"
                          prop="auditorId">
              <el-select v-model="saveForm.auditorId"
                         placeholder="请选择审核人"
                         filterable
                         clearable>
                <el-option v-for="user in userList"
                           :key="user.id"
                           :label="userDisplayName(user)"
                           :value="user.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item label="订单备注"
                          prop="remark">
              <el-input v-model="saveForm.remark"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <el-row>
        <el-col :offset="20">
          <el-button @click="save">保存</el-button>
          <el-button type="primary"
                     @click="saveThenNew">保存并新增</el-button>
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
            <el-form-item label="销售单价"
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
            <el-form-item label="金额"
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

import orderSaveUserMixin from '@/mixins/orderSaveUser'
import orderSaveSettlementMixin from '@/mixins/orderSaveSettlement'

export default {
  mixins: [orderSaveUserMixin, orderSaveSettlementMixin],
  watch: {
    '$route' () {
      this.bootstrapSalePage()
    },
    // 数量/单价变更时，按当前编辑模式自动重算
    'saveProductForm.quantity': 'handleQuantityOrPriceChanged',
    'saveProductForm.price': 'handleQuantityOrPriceChanged',
    // 折扣率和折扣额双向联动，分别走不同计算分支
    'saveProductForm.discountRate': 'handleDiscountRateChanged',
    'saveProductForm.discountAmount': 'handleDiscountAmountChanged',
    // 结算信息联动：优惠三字段互算 + 退款/欠款互算（含客户承担费用）
    'saveForm.preferentialRate': 'handlePreferentialRateChanged',
    'saveForm.preferentialAmount': 'handlePreferentialAmountChanged',
    'saveForm.preferredAmount': 'handlePreferredAmountChanged',
    'saveForm.currentAmount': 'handleCurrentAmountChanged',
    'saveForm.debtAmount': 'handleDebtAmountChanged',
    'saveForm.customerFee': 'handleCustomerFeeChanged',
    'saveForm.productList': {
      handler: 'handleSaleProductListChanged',
      deep: true
    }
  },
  data() {
    return {
      // 客户列表
      customerList: [],
      customerLevelList: [],
      sellerList: [],
      contactList: [],
      saveForm: {
        type: 'returned',
        preferentialRate: 0,
        preferentialAmount: 0.0,
        preferredAmount: 0,
        customerFee: 0.0,
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
      settlementCalcMode: 'rate',
      // current: 以本次退款为主输入；debt: 以本次欠款为主输入
      paymentCalcMode: 'current',
      isSettlementSyncing: false,
      isPaymentSyncing: false
    }
  },
  created() {
    this.bootstrapSalePage()
  },
  methods: {
    bootstrapSalePage() {
      const saleId = this.$route.query.saleId
      if (saleId !== undefined) { // 如果存在 if，则调用后端API，获取 销退单 详细信息
        console.log(saleId)
        this.getSaleDetail(saleId)
      } else { // 如果不存在 else，则调用后端API，生成新的 销退单 单据编号
        this.getSaleCode()
      }

      // 获取 客户 列表
      this.getCustomerList()
      // 获取 客户等级 列表
      this.getCustomerLevelList()
      // 获取 销售人员 列表
      this.getSellerList()
      // 获取 仓库 列表
      this.getWarehouseList()
      // 获取 结算账户 列表
      this.getSettlementAccountList()
    },
    // 获取客户列表
    async getCustomerList() {
      const { data: result } = await this.$http.post('/customer/page', {
        current: 1,
        size: 10000
      })
      if (!result.success) return this.$message.error(result.message)

      this.customerList = result.data.customerPage.records
    },
    // 获取客户等级列表
    async getCustomerLevelList() {
      const { data: result } = await this.$http.post('/dict/itemList', {
        dictCode: 'customer_level'
      })
      if (!result.success) return this.$message.error(result.message)

      this.customerLevelList = result.data.itemList
    },
    // 获取销售人员列表
    async getSellerList() {
      const { data: result } = await this.$http.post('/employee/page', {
        current: 1,
        size: 10000
      })
      if (!result.success) return this.$message.error(result.message)

      this.sellerList = result.data.employeePage.records
    },
    // 获取单据编号
    async getSaleCode() {
      const { data: result } = await this.$http.post('/sale/createCode', {
        type: 'returned'
      })
      if (!result.success) return this.$message.error(result.message)

      this.saveForm = {
        id: '',
        type: 'returned',
        preferentialRate: 0,
        preferentialAmount: 0.0,
        preferredAmount: 0,
        customerFee: 0.0,
        currentAmount: 0.0,
        debtAmount: 0.0,
        productList: [],
        accountList: []
      }

      this.saveForm.code = result.data.code
      this.applyDefaultLister()
      this.recalculateSettlementByMode()
    },
    // 获取详情
    async getSaleDetail(id) {
      const { data: result } = await this.$http.post('/sale/detail', {
        saleId: id
      })
      if (!result.success) return this.$message.error(result.message)

      console.log(result.data)
      this.saveForm = result.data.sale
      this.applyAccountIdFromSaveForm()
      this.recalculateSettlementByMode()
    },

    // 获取客户联系人列表
    async getContactList(customerId) {
      const { data: result } = await this.$http.post('/customer/contactList', {
        customerId: customerId
      })
      if (!result.success) return this.$message.error(result.message)

      this.contactList = result.data.contactList
    },

    // 选择了客户
    selectCustomerChanged(customerId) {
      this.getContactList(customerId)
    },

    // 选择了联系人
    selectContactChanged(contactName) {
      const contact = this.$_.find(this.contactList, { name: contactName })
      if (contact === undefined) {
        this.saveForm.address = ''
        this.saveForm.phone = ''
        return
      }
      this.saveForm.address = contact.address || ''
      this.saveForm.phone = contact.phone || contact.mobile || ''
    },

    // 选择了结算账户
    selectAccountChanged(accountId) {
      this.saveForm.accountList = []
      var account = {
        accountId
      }
      this.saveForm.accountList.push(account)
    },
    // 点击按钮，保存销货退货单
    async save() {
      this.$refs.saveFormRef.validate(async (valid) => {
        if (!valid) return
        // 可以发起新增销货退货单的网络请求
        const { data: result } = await this.$http.post('/sale/save', {
          sale: this.saveForm,
          productList: this.saveForm.productList,
          accountList: this.saveForm.accountList
        })
        if (!result.success) {
          return this.$message.error(result.message)
        }

        this.$message.success('保存销货退货单成功！')
        console.log(result)
        this.saveForm.id = result.data.sale.id
      })
    },
    // 保存并新增
    async saveThenNew() {
      this.save()
      this.getSaleCode()
    },
    // 显示保存商品对话框
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
    // 显示修改商品对话框
    showProductEditDialog(contact, index) {
      this.isProductAdd = false
      this.discountCalcMode = 'rate'
      this.saveProductForm = this.$_.cloneDeep(contact)
      this.modifyProductIndex = index
      this.saveProductDialogVisible = true
    },

    // 监听新增商品对话框的关闭事件
    saveProductDialogClosed() {
      if (this.isProductAdd) {
        this.$refs.saveProductFormRef.resetFields()
      }
    },
    // 点击按钮，添加商品
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
    // 删除商品
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
    // 显示选择商品对话框
    showSelectProductDialog() {
      this.selectProductDialogVisible = true
    },
    // 选择好商品
    handleSelectProduct(product) {
      if (product != null) {
        this.saveProductForm.productName = product.name
        this.saveProductForm.productId = product.id
        this.saveProductForm.unitName = product.unitName
        const customerLevelName = this.getCurrentCustomerLevelName()
        this.saveProductForm.price = this.getProductPriceByCustomerLevel(product, customerLevelName)
        this.saveProductForm.discountRate = this.getProductDiscountRateByCustomerLevel(product, customerLevelName)
        this.discountCalcMode = 'rate'
        this.calculateByDiscountRate()
      }
    },
    // 获取当前客户等级名称（如：零售客户、批发客户）
    getCurrentCustomerLevelName() {
      const customer = this.$_.find(this.customerList, { id: this.saveForm.customerId })
      if (customer === undefined) {
        return ''
      }
      const level = this.$_.find(this.customerLevelList, { id: customer.level })
      return level === undefined ? '' : level.name
    },
    // 根据客户等级获取销售单价
    getProductPriceByCustomerLevel(product, levelName) {
      if (levelName === '批发客户') {
        return this.toFixedNumber(this.toNumber(product.wholesalePrice || product.retailPrice || product.price))
      }
      if (levelName === 'VIP客户') {
        return this.toFixedNumber(this.toNumber(product.vipPrice || product.retailPrice || product.price))
      }
      // 零售客户/折扣等级一/折扣等级二：默认按零售价
      return this.toFixedNumber(this.toNumber(product.retailPrice || product.price))
    },
    // 根据客户等级获取默认折扣率
    getProductDiscountRateByCustomerLevel(product, levelName) {
      if (levelName === '折扣等级一') {
        return this.toFixedNumber(this.toNumber(product.discountRate1 || product.discountRate))
      }
      if (levelName === '折扣等级二') {
        return this.toFixedNumber(this.toNumber(product.discountRate2 || product.discountRate))
      }
      return this.toFixedNumber(this.toNumber(product.discountRate))
    },
    onDiscountRateInput() {
      // 用户正在改折扣率，后续计算优先由折扣率驱动
      this.discountCalcMode = 'rate'
    },
    onDiscountAmountInput() {
      // 用户正在改折扣额，后续计算优先由折扣额驱动
      this.discountCalcMode = 'amount'
    },
    handleQuantityOrPriceChanged() {
      this.recalculateByMode()
    },
    handleDiscountRateChanged() {
      if (this.isDiscountSyncing || this.discountCalcMode !== 'rate') {
        return
      }
      this.calculateByDiscountRate()
    },
    handleDiscountAmountChanged() {
      if (this.isDiscountSyncing || this.discountCalcMode !== 'amount') {
        return
      }
      this.calculateByDiscountAmount()
    },
    recalculateByMode() {
      if (this.discountCalcMode === 'amount') {
        this.calculateByDiscountAmount()
      } else {
        this.calculateByDiscountRate()
      }
    },
    // 公式：折扣额 = 数量 * 单价 * 折扣率；销售金额 = 小计 - 折扣额
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
    // 公式：折扣率 = 折扣额 / (数量 * 单价)；销售金额 = 小计 - 折扣额
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
    // 折扣率兼容两种输入：0~1（小数）和 0~100（百分比）
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
    discountAmountToRate(subtotal, discountAmount, currentRate) {
      if (subtotal <= 0) {
        return 0
      }
      const rateByRatio = discountAmount / subtotal
      // 保持当前折扣率输入习惯：若此前按百分比输入，则继续输出百分比
      return this.toNumber(currentRate) > 1 ? rateByRatio * 100 : rateByRatio
    },
    // 折扣额限制在 [0, 小计]，防止出现负金额
    limitDiscountAmount(subtotal, discountAmount) {
      if (subtotal <= 0) {
        return 0
      }
      if (discountAmount <= 0) {
        return 0
      }
      return discountAmount > subtotal ? subtotal : discountAmount
    },
    onPreferentialRateInput() {
      this.settlementCalcMode = 'rate'
    },
    onPreferentialAmountInput() {
      this.settlementCalcMode = 'amount'
    },
    onPreferredAmountInput() {
      this.settlementCalcMode = 'preferred'
    },
    onCurrentAmountInput() {
      this.paymentCalcMode = 'current'
    },
    onDebtAmountInput() {
      this.paymentCalcMode = 'debt'
    },
    onCustomerFeeInput() {
      this.recalculatePaymentByMode()
    },
    handleSaleProductListChanged() {
      this.recalculateSettlementByMode()
    },
    handlePreferentialRateChanged() {
      if (this.isSettlementSyncing || this.settlementCalcMode !== 'rate') {
        return
      }
      this.calculateSettlementByRate()
    },
    handlePreferentialAmountChanged() {
      if (this.isSettlementSyncing || this.settlementCalcMode !== 'amount') {
        return
      }
      this.calculateSettlementByAmount()
    },
    handlePreferredAmountChanged() {
      if (!this.isPaymentSyncing) {
        this.recalculatePaymentByMode()
      }
      if (this.isSettlementSyncing || this.settlementCalcMode !== 'preferred') {
        return
      }
      this.calculateSettlementByPreferredAmount()
    },
    handleCurrentAmountChanged() {
      if (this.isPaymentSyncing || this.paymentCalcMode !== 'current') {
        return
      }
      this.calculateByCurrentAmount()
    },
    handleDebtAmountChanged() {
      if (this.isPaymentSyncing || this.paymentCalcMode !== 'debt') {
        return
      }
      this.calculateByDebtAmount()
    },
    handleCustomerFeeChanged() {
      if (!this.isPaymentSyncing) {
        this.recalculatePaymentByMode()
      }
    },
    recalculateSettlementByMode() {
      if (this.settlementCalcMode === 'amount') {
        this.calculateSettlementByAmount()
      } else if (this.settlementCalcMode === 'preferred') {
        this.calculateSettlementByPreferredAmount()
      } else {
        this.calculateSettlementByRate()
      }
    },
    recalculatePaymentByMode() {
      if (this.paymentCalcMode === 'debt') {
        this.calculateByDebtAmount()
      } else {
        this.calculateByCurrentAmount()
      }
    },
    // 公式：优惠金额 = 商品销售总金额 * 优惠率；优惠后金额 = 商品销售总金额 - 优惠金额
    calculateSettlementByRate() {
      const totalAmount = this.getSaleProductTotalAmount()
      const preferentialRate = this.toNumber(this.saveForm.preferentialRate)
      const preferentialAmount = this.rateToDiscountAmount(totalAmount, preferentialRate)
      const preferredAmount = totalAmount - preferentialAmount
      this.syncSettlementFields({
        preferentialAmount,
        preferredAmount
      })
    },
    // 公式：优惠率 = 优惠金额 / 商品销售总金额；优惠后金额 = 商品销售总金额 - 优惠金额
    calculateSettlementByAmount() {
      const totalAmount = this.getSaleProductTotalAmount()
      const rawPreferentialAmount = this.toNumber(this.saveForm.preferentialAmount)
      const preferentialAmount = this.limitDiscountAmount(totalAmount, rawPreferentialAmount)
      const preferentialRate = this.discountAmountToRate(totalAmount, preferentialAmount, this.saveForm.preferentialRate)
      const preferredAmount = totalAmount - preferentialAmount
      this.syncSettlementFields({
        preferentialRate,
        preferentialAmount,
        preferredAmount
      })
    },
    // 公式：优惠金额 = 商品销售总金额 - 优惠后金额；优惠率 = 优惠金额 / 商品销售总金额
    calculateSettlementByPreferredAmount() {
      const totalAmount = this.getSaleProductTotalAmount()
      const rawPreferredAmount = this.toNumber(this.saveForm.preferredAmount)
      const preferredAmount = this.limitDiscountAmount(totalAmount, rawPreferredAmount)
      const preferentialAmount = totalAmount - preferredAmount
      const preferentialRate = this.discountAmountToRate(totalAmount, preferentialAmount, this.saveForm.preferentialRate)
      this.syncSettlementFields({
        preferentialRate,
        preferentialAmount,
        preferredAmount
      })
    },
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
    // 公式：本次欠款 = 优惠后金额 + 客户承担费用 - 本次退款
    calculateByCurrentAmount() {
      const receivableTotal = this.toNumber(this.saveForm.preferredAmount) + this.toNumber(this.saveForm.customerFee)
      const currentAmount = this.limitDiscountAmount(receivableTotal, this.saveForm.currentAmount)
      const debtAmount = receivableTotal - currentAmount
      this.syncPaymentFields({
        currentAmount,
        debtAmount
      })
    },
    // 公式：本次退款 = 优惠后金额 + 客户承担费用 - 本次欠款
    calculateByDebtAmount() {
      const receivableTotal = this.toNumber(this.saveForm.preferredAmount) + this.toNumber(this.saveForm.customerFee)
      const debtAmount = this.limitDiscountAmount(receivableTotal, this.saveForm.debtAmount)
      const currentAmount = receivableTotal - debtAmount
      this.syncPaymentFields({
        currentAmount,
        debtAmount
      })
    },
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
    getSaleProductTotalAmount() {
      return this.toFixedNumber(
        this.saveForm.productList.reduce(
          (sum, product) => sum + this.toNumber(product.amount),
          0
        )
      )
    },
    toNumber(value) {
      const numberValue = Number(value)
      if (Number.isNaN(numberValue)) {
        return 0
      }
      return numberValue
    },
    toFixedNumber(value) {
      return Number(this.toNumber(value).toFixed(2))
    },
    // 获取仓库列表
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
