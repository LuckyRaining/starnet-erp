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
