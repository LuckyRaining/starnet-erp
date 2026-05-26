import {
  applyAccountIdFromRecords,
  fetchSettlementAccountList
} from '@/utils/settlementAccount'

/**
 * 单据保存页：结算账户下拉（settlementAccountList 与 saveForm.accountList 分离）
 */
export default {
  data () {
    return {
      settlementAccountList: []
    }
  },

  methods: {
    /**
     * 加载 结算账户 列表。
     */
    async getSettlementAccountList () {
      try {
        this.settlementAccountList = await fetchSettlementAccountList(this.$http)
      } catch (e) {
        this.settlementAccountList = []
        this.$message.error(e.message || '获取结算账户列表失败')
      }
    },

    /**
     * 将 结算账户ID 从 保存表单 应用到 保存表单。
     */
    applyAccountIdFromSaveForm () {
      applyAccountIdFromRecords(this.saveForm, this)
    }
  }
}
