/**
 * 加载结算账户下拉列表（与 saveForm.accountList 提交数据区分，避免字段同名冲突）
 */
export async function fetchSettlementAccountList (http) {
  const { data: result } = await http.post('/settlementAccount/list', {})
  if (!result.success) {
    throw new Error(result.message || '获取结算账户列表失败')
  }

  return result.data.accountList || []
}

/** 编辑单据：从 accountList 记录回填 accountId 供下拉展示 */
export function applyAccountIdFromRecords (saveForm, vm) {
  // 若 saveForm.accountId 已设置，则不进行回填
  if (!saveForm || saveForm.accountId) {
    return
  }

  // 若 saveForm.accountList 为空，则不进行回填
  const records = saveForm.accountList
  if (!records || !records.length) {
    return
  }

  // 获取 结算账户列表 中的 第一个 结算账户ID
  const accountId = records[0].accountId
  if (accountId != null && accountId !== '') {
    // 将 结算账户ID 设置到 saveForm.accountId 中
    vm.$set(saveForm, 'accountId', accountId)
  }
}
