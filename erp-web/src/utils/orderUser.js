const USER_SESSION_KEY = 'user'

/**
 * 保存 当前登录用户（与后端 User 对象字段一致：id、username、name、mobile）
 */
export function setCurrentUser(user) {
  // 若 登录用户对象 为空（未登录），则删除 当前登录用户 的 sessionStorage 中的记录
  if (!user) {
    window.sessionStorage.removeItem(USER_SESSION_KEY)
    return
  }

  window.sessionStorage.setItem(USER_SESSION_KEY, JSON.stringify({
    id: user.id,
    username: user.username,
    name: user.name,
    mobile: user.mobile
  }))
}

/**
 * 获取 当前登录用户
 */
export function getCurrentUser () {
  const raw = window.sessionStorage.getItem(USER_SESSION_KEY)
  if (!raw) {
    return null
  }

  try {
    return JSON.parse(raw)
  } catch (e) {
    return null
  }
}

/**
 * 获取 当前登录用户的 ID
 *
 * @returns {string} 用户 ID，若不存在则返回空字符串
 */
export function getCurrentUserId () {
  const user = getCurrentUser()
  return user && user.id != null && user.id !== '' ? String(user.id) : ''
}

/**
 * 获取 用户的显示名称
 *
 * @param {Object} user 用户对象
 *
 * @returns {string} 优先返回 name，若不存在则返回 username
 */
export function userDisplayName (user) {
  return user.name || user.username
}

/**
 * 获取 所有用户列表（用于下拉选择等场景）
 *
 * @param {Object} http Axios 实例
 *
 * @returns {Array} 用户记录数组
 */
export async function fetchUserList (http) {
  const { data: result } = await http.post('/user/page', {
    current: 1,
    size: 1000
  })
  if (!result.success) {
    throw new Error(result.message || '获取用户列表失败')
  }
  return result.data.userPage.records
}

/**
 * 为保存表单应用默认的制单人（listerId）。
 *
 * 若表单中未设置 listerId，则自动填入当前登录用户的 ID
 *
 * @param {Object} saveForm 业务单据的保存表单对象
 */
export function applyDefaultLister (saveForm) {
  const userId = getCurrentUserId()
  if (userId && !saveForm.listerId) {
    saveForm.listerId = userId
  }
}

/** 列表页切换审核但未登录时的提示文案 */
export const SWITCH_CHECK_LOGIN_REQUIRED_MESSAGE = '当前未登录，请登陆后审核！'

/** 列表页尝试取消审核时的提示文案（所有单据均为单向审核） */
export const SWITCH_CHECK_UNCHECK_NOT_ALLOWED_MESSAGE = '已审核的单据不能取消审核！'

/**
 * 列表页切换审核状态时组装 *SwitchCheck 请求参数。
 * <p>
 * 规则：所有单据均为单向审核（仅 false → true）。
 * <ul>
 *   <li>checked 为 false：拒绝取消审核</li>
 *   <li>checked 为 true：必须已登录，自动附带当前用户 id 为 auditorId</li>
 * </ul>
 *
 * @param {string} idParam 主键字段名
 * @param {string|number} id 单据 ID
 * @param {boolean} checked 切换后的审核状态
 * @returns {{ ok: boolean, payload?: Object, message?: string }}
 */
export function buildSwitchCheckPayload (idParam, id, checked) {
  const payload = { [idParam]: id }

  // 单向审核：不允许取消
  if (!checked) {
    return { ok: false, message: SWITCH_CHECK_UNCHECK_NOT_ALLOWED_MESSAGE }
  }

  const auditorId = getCurrentUserId()
  if (!auditorId) {
    return { ok: false, message: SWITCH_CHECK_LOGIN_REQUIRED_MESSAGE }
  }

  payload.auditorId = auditorId
  return { ok: true, payload }
}

/**
 * 审核成功后回填列表行的审核人 ID 与展示名称。
 * 优先使用后端返回的 auditorId；若为空则回退为当前登录用户。
 */
export function applyAuditorToRow(row, entity) {
  // 如果行数据或审核状态为空，则不进行处理
  if (!row || !row.checked) {
    return
  }

  if (entity && entity.auditorId) {
    // 如果实体对象有审核人，则设置行数据的审核人
    row.auditorId = entity.auditorId
  } else {
    // 如果实体对象没有审核人，则设置行数据的审核人为当前登录用户
    row.auditorId = getCurrentUserId()
  }

  // 获取当前登录用户
  const user = getCurrentUser()
  if (user) {
    row.auditorName = userDisplayName(user)
  }
}
