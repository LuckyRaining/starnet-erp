import {
  applyAuditorToRow,
  buildSwitchCheckPayload
} from '@/utils/orderUser'

/**
 * 列表页审核开关 mixin。
 * <p>
 * 各业务 List.vue 引入后调用 {@link #postSwitchCheck}，统一处理：
 * <ul>
 *   <li>仅允许 false → true（已审核后开关禁用，取消审核会被拒绝）</li>
 *   <li>切换为已审核时，自动携带当前登录用户为 auditorId</li>
 *   <li>未登录则回滚开关并提示</li>
 *   <li>成功后回填审核人展示字段</li>
 * </ul>
 */
export default {
  methods: {
    /**
     * 切换审核状态并调用后端 *SwitchCheck 接口
     *
     * @param {string} url 接口地址，如 /purchase/switchCheck
     * @param {string} idParam 主键参数名，如 purchaseId
     * @param {Object} row 当前表格行（含 id、code、checked）
     * @param {string} entityKey 响应 data 中的实体键名，如 purchase
     * @param {string} successMessage 审核成功时的提示文案
     */
    async postSwitchCheck (url, idParam, row, entityKey, successMessage) {
      // 组装请求参数：审核为 true 时会校验登录并附带 auditorId
      const prep = buildSwitchCheckPayload(idParam, row.id, row.checked)

      // 未登录等前置校验失败：回滚开关，避免 UI 与后端状态不一致
      if (!prep.ok) {
        row.checked = !row.checked
        return this.$message.warning(prep.message)
      }

      const { data: result } = await this.$http.post(url, prep.payload)
      if (!result.success) {
        // 接口失败同样回滚开关
        row.checked = !row.checked
        return this.$message.error(result.message || '订单【' + row.code + '】审核失败！')
      }

      // 用后端返回的最新实体同步 checked 与审核人展示
      const entity = result.data[entityKey]
      row.checked = entity.checked
      applyAuditorToRow(row, entity)
      if (entity.checked && successMessage) {
        this.$message.success(successMessage)
      }
    }
  }
}
