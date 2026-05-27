/**
 * 为分类树节点附加商品总数（含子分类下的商品）。
 *
 * @param {Array} categories 分类列表
 * @param {Object} countMap 各分类直接归属的商品数量
 * @returns {number} 当前层级商品总数
 */
export function attachCategoryProductCount (categories, countMap) {
  if (!categories || !categories.length) {
    return 0
  }

  let levelTotal = 0
  categories.forEach((category) => {
    let count = Number(countMap[category.id] || 0)

    if (category.childList && category.childList.length) {
      // 递归统计 子分类的商品数量（含子分类下的商品），并累加到 当前分类的 商品数量中
      count += attachCategoryProductCount(category.childList, countMap)
    }

    category.productCount = count
    levelTotal += count
  })
  // 返回当前层级商品总数
  return levelTotal
}
