<template>
	<view class="page">
		<view v-if="loading" class="state">加载系统参数中…</view>

		<template v-else>
			<view class="card">
				<view class="title">系统参数（服务端）</view>
				<text class="hint">数据来自接口「获取系统配置」；保存将调用「设置系统配置」。公司名称与地址必填。</text>

				<view class="field">
					<text class="label">公司名称 *</text>
					<uni-easyinput v-model="configuration.companyName" placeholder="请输入公司名称" />
				</view>
				<view class="field">
					<text class="label">公司地址 *</text>
					<uni-easyinput v-model="configuration.companyAddress" placeholder="请输入公司地址" />
				</view>
				<view class="field">
					<text class="label">公司电话</text>
					<uni-easyinput v-model="configuration.companyPhone" placeholder="公司电话" />
				</view>
				<view class="field">
					<text class="label">公司传真</text>
					<uni-easyinput v-model="configuration.companyFax" placeholder="公司传真" />
				</view>
				<view class="field">
					<text class="label">公司邮编</text>
					<uni-easyinput v-model="configuration.companyPostCode" placeholder="公司邮编" />
				</view>
				<view class="field field-row">
					<text class="label">启用时间</text>
					<text class="readonly">{{ displayStartTime }}</text>
				</view>
				<view class="field field-row">
					<text class="label">本位币</text>
					<text class="readonly">{{ configuration.currency || '—' }}</text>
				</view>
				<view class="field field-row">
					<text class="label">存货计价</text>
					<text class="readonly">{{ inventoryValuationLabel }}</text>
				</view>
				<view class="field">
					<text class="label">数量小数位</text>
					<uni-easyinput v-model="quantityPrecisionStr" type="number" placeholder="0–5" />
				</view>
				<view class="field">
					<text class="label">单价小数位</text>
					<uni-easyinput v-model="pricePrecisionStr" type="number" placeholder="0–5" />
				</view>
				<view class="field field-row">
					<text class="label">检查负库存</text>
					<switch
						:checked="configuration.checkNegativeStock"
						color="#C00000"
						@change="onNegStockChange"
					/>
				</view>

				<button class="btn" type="primary" :loading="saving" @click="saveServer">保存到服务器</button>
				<button class="btn plain" :disabled="saving" @click="loadServer">重新加载</button>
			</view>

			<view class="card">
				<view class="title">小程序默认项（仅本机）</view>
				<text class="hint">以下仅保存在本机，用于采购待支付页快速带出默认供应商、结算账户等，不参与系统配置接口。</text>

				<uni-easyinput v-model="localForm.defaultCustomerId" placeholder="默认客户 ID（可选）" />
				<uni-easyinput v-model="localForm.defaultWarehouseId" placeholder="默认仓库 ID（可选）" />
				<uni-easyinput v-model="localForm.defaultSupplierId" placeholder="默认供应商 ID（可选）" />
				<uni-easyinput v-model="localForm.defaultSettlementAccountId" placeholder="默认结算账户 ID（可选）" />
				<uni-easyinput v-model="localForm.defaultListerId" placeholder="默认制单人职员 ID（可选）" />

				<button class="btn secondary" @click="saveLocal">保存本机默认</button>
			</view>
		</template>
	</view>
</template>

<script>
export default {
	data() {
		return {
			loading: true,
			saving: false,
			configuration: {
				companyName: '',
				companyAddress: '',
				companyPhone: '',
				companyFax: '',
				companyPostCode: '',
				startTime: null,
				currency: 'RMB',
				quantityPrecision: 2,
				pricePrecision: 2,
				inventoryValuationMethod: 10,
				checkNegativeStock: true
			},
			quantityPrecisionStr: '2',
			pricePrecisionStr: '2',
			localForm: {
				defaultCustomerId: '',
				defaultWarehouseId: '',
				defaultSupplierId: '',
				defaultSettlementAccountId: '',
				defaultListerId: ''
			}
		};
	},

	computed: {
		displayStartTime() {
			const t = this.configuration && this.configuration.startTime;
			if (t == null || t === '') return '—';
			if (typeof t === 'string') {
				return t.length >= 16 ? t.slice(0, 16).replace('T', ' ') : t;
			}
			return '—';
		},

		inventoryValuationLabel() {
			const m = this.configuration && this.configuration.inventoryValuationMethod;
			if (m === 10) return '移动平均法';
			return m != null ? String(m) : '—';
		}
	},

	onShow() {
		const saved = uni.getStorageSync('STAR_NET_COMPANY_SETTING');
		if (saved && typeof saved === 'object') {
			this.localForm = { ...this.localForm, ...saved };
		}
	},

	async onLoad() {
		await this.loadServer();
	},

	methods: {
		onNegStockChange(e) {
			this.configuration.checkNegativeStock = !!(e.detail && e.detail.value);
		},

		applyConfiguration(raw) {
			const c = raw || {};
			this.configuration = {
				companyName: c.companyName != null ? String(c.companyName) : '',
				companyAddress: c.companyAddress != null ? String(c.companyAddress) : '',
				companyPhone: c.companyPhone != null ? String(c.companyPhone) : '',
				companyFax: c.companyFax != null ? String(c.companyFax) : '',
				companyPostCode: c.companyPostCode != null ? String(c.companyPostCode) : '',
				startTime: c.startTime,
				currency: c.currency != null ? String(c.currency) : 'RMB',
				quantityPrecision: Math.min(5, Math.max(0, parseInt(c.quantityPrecision, 10) || 0)),
				pricePrecision: Math.min(5, Math.max(0, parseInt(c.pricePrecision, 10) || 0)),
				inventoryValuationMethod:
					c.inventoryValuationMethod != null ? Number(c.inventoryValuationMethod) : 10,
				checkNegativeStock: c.checkNegativeStock !== false
			};
			this.quantityPrecisionStr = String(this.configuration.quantityPrecision);
			this.pricePrecisionStr = String(this.configuration.pricePrecision);
		},

		buildPayload() {
			const qp = Math.min(5, Math.max(0, parseInt(this.quantityPrecisionStr, 10) || 0));
			const pp = Math.min(5, Math.max(0, parseInt(this.pricePrecisionStr, 10) || 0));
			return {
				companyName: String(this.configuration.companyName || '').trim(),
				companyAddress: String(this.configuration.companyAddress || '').trim(),
				companyPhone: String(this.configuration.companyPhone || '').trim(),
				companyFax: String(this.configuration.companyFax || '').trim(),
				companyPostCode: String(this.configuration.companyPostCode || '').trim(),
				quantityPrecision: qp,
				pricePrecision: pp,
				checkNegativeStock: !!this.configuration.checkNegativeStock
			};
		},

		async loadServer() {
			this.loading = true;
			try {
				const data = await this.$api.getSystemConfiguration({});
				const cfg = data && data.configuration;
				if (cfg) {
					this.applyConfiguration(cfg);
				} else {
					uni.showToast({ title: '未获取到系统配置', icon: 'none' });
				}
			} catch (e) {
				/* request 已 toast */
			} finally {
				this.loading = false;
			}
		},

		async saveServer() {
			const p = this.buildPayload();
			if (!p.companyName) {
				uni.showToast({ title: '请输入公司名称', icon: 'none' });
				return;
			}
			if (!p.companyAddress) {
				uni.showToast({ title: '请输入公司地址', icon: 'none' });
				return;
			}

			this.saving = true;
			try {
				const data = await this.$api.setSystemConfiguration({ configuration: p });
				if (data && data.configuration) {
					this.applyConfiguration(data.configuration);
				}
				uni.showToast({ title: '保存成功', icon: 'success' });
			} catch (e) {
				/* request 已 toast */
			} finally {
				this.saving = false;
			}
		},

		saveLocal() {
			uni.setStorageSync('STAR_NET_COMPANY_SETTING', { ...this.localForm });
			uni.showToast({ title: '本机默认已保存', icon: 'success' });
		}
	}
};
</script>

<style lang="scss">
.page {
	padding: 20rpx;
	min-height: 100vh;
	box-sizing: border-box;
	background: #f8f8f8;
}

.state {
	text-align: center;
	padding: 120rpx 40rpx;
	color: #888;
	font-size: 28rpx;
}

.card {
	background: #fff;
	border-radius: 16rpx;
	padding: 24rpx;
	margin-bottom: 24rpx;
	display: flex;
	flex-direction: column;
	gap: 16rpx;
}

.title {
	font-size: 30rpx;
	font-weight: 600;
	color: #333;
}

.hint {
	font-size: 24rpx;
	color: #888;
	line-height: 1.45;
	margin-bottom: 8rpx;
}

.field {
	display: flex;
	flex-direction: column;
	gap: 10rpx;
}

.field-row {
	flex-direction: row;
	align-items: center;
	justify-content: space-between;
}

.label {
	font-size: 26rpx;
	color: #555;
}

.readonly {
	font-size: 26rpx;
	color: #333;
}

.btn {
	margin-top: 8rpx;
}

.btn.plain {
	background: #f2f2f2;
	color: #333;
}

.btn.secondary {
	background: #fff;
	color: #c00000;
	border: 1px solid #c00000;
}
</style>
