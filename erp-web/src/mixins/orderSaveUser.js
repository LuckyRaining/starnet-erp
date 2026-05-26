import {
  fetchUserList,
  getCurrentUser,
  getCurrentUserId,
  userDisplayName
} from '@/utils/orderUser'

/**
 * 单据保存页：制单人/审核人下拉（用户列表 + 新增时默认当前用户为制单人）
 */
export default {
  data() {
    return {
      userList: []
    }
  },

  watch: {
    // 用户列表加载完成后，再补一次默认制单人（解决 getCode 先于 userList 完成的竞态）
    userList() {
      this.syncDefaultLister()
    }
  },

  async created() {
    try {
      this.userList = await fetchUserList(this.$http)
      this.syncDefaultLister()
    } catch (e) {
      this.$message.error(e.message)
    }
  },

  methods: {
    userDisplayName,

    /** 校验 当前登录用户若不在分页列表中 */
    isCurrentUserInList() {
      const current = getCurrentUser()
      if (!current || !current.id) {
        return
      }

      const currentId = String(current.id)
      const exists = this.userList.some(u => String(u.id) === currentId)
      if (exists) {
        // console.log('当前登录用户 在用户列表中！')
      } else {
        // console.log('当前登录用户 不在用户列表中！')
      }

      // if (!exists) {
      //   this.userList.unshift({
      //     id: current.id,
      //     username: current.username,
      //     name: current.name,
      //     mobile: current.mobile
      //   })
      // }
    },

    /** 新增单据：默认制单人为当前登录用户（供 getCode 与 mixin 共用） */
    applyDefaultLister() {
      this.syncDefaultLister()
    },

    syncDefaultLister() {
      if (!this.saveForm || this.saveForm.id) {
        return
      }

      this.isCurrentUserInList()

      const userId = getCurrentUserId()
      if (!userId) {
        return
      }

      // 若 saveForm.listerId 未设置，则设置 默认制单人 为 当前登录用户
      if (!this.saveForm.listerId) {
        // console.log('userId: ' + userId)
        // console.log('listerId: ' + this.saveForm.listerId)
        this.$set(this.saveForm, 'listerId', String(userId))
        // console.log('listerId: ' + this.saveForm.listerId)
      }
    }
  }
}
