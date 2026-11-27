<script setup>
// 首页仅提供各业务模块的快捷入口，数据均来自实际业务页面
</script>

<template>
  <div class="home">
    <header class="home-header">
      <h1 class="home-title">教育培训管理系统</h1>
      <p class="home-subtitle">
        部门、员工、班级、学员与教学数据的一体化管理平台
      </p>
    </header>

    <section class="card-grid">
      <router-link class="info-card" to="/clazz">
        <h2 class="info-card-title">班级管理</h2>
        <p class="info-card-desc">班级信息维护、开课结课状态管理</p>
        <span class="info-card-arrow">进入 →</span>
      </router-link>

      <router-link class="info-card" to="/stu">
        <h2 class="info-card-title">学员管理</h2>
        <p class="info-card-desc">学员档案、班级分配与违纪处理</p>
        <span class="info-card-arrow">进入 →</span>
      </router-link>

      <router-link class="info-card" to="/emp">
        <h2 class="info-card-title">员工管理</h2>
        <p class="info-card-desc">员工信息、部门归属与工作经历维护</p>
        <span class="info-card-arrow">进入 →</span>
      </router-link>

      <router-link class="info-card" to="/dept">
        <h2 class="info-card-title">部门管理</h2>
        <p class="info-card-desc">组织架构与部门信息维护</p>
        <span class="info-card-arrow">进入 →</span>
      </router-link>
    </section>

    <section class="quick-links">
      <h2 class="section-title">数据统计</h2>
      <div class="link-row">
        <router-link class="quick-link" to="/report/emp"
          >员工信息统计</router-link
        >
        <router-link class="quick-link" to="/report/stu"
          >学员信息统计</router-link
        >
        <router-link class="quick-link" to="/log">操作日志</router-link>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* layout 的白色主面板内边距为 24px；用负外边距把画布铺满，
   再以 32px 内边距承载内容，形成 #f5f5f2 画布 + 白色卡片 */
.home {
  box-sizing: border-box;
  min-height: calc(100% + 48px);
  margin: -24px;
  padding: 32px 32px 40px;
  background-color: #f5f5f2;
  border-radius: 14px;
  color: #1d1d1f;
}

.home-header {
  margin-bottom: 32px;
}

.home-title {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  line-height: 1.2;
  letter-spacing: -0.02em;
}

.home-subtitle {
  margin: 10px 0 0;
  color: #86868b;
  font-size: 14px;
  line-height: 1.5;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.info-card {
  display: flex;
  flex-direction: column;
  padding: 20px;
  background-color: #fff;
  border: 1px solid #e8e8e3;
  border-radius: 14px;
  color: inherit;
  text-decoration: none;
  transition: border-color 0.15s, transform 0.15s;
}

.info-card:hover {
  border-color: #1d1d1f;
  transform: translateY(-2px);
}

.info-card-title {
  margin: 0;
  font-size: 15px;
  font-weight: 500;
  line-height: 1.4;
}

.info-card-desc {
  margin: 8px 0 0;
  color: #86868b;
  font-size: 13px;
  line-height: 1.6;
}

.info-card-arrow {
  margin-top: 20px;
  color: #6e6e73;
  font-size: 13px;
}

.quick-links {
  margin-top: 40px;
}

.section-title {
  margin: 0 0 12px;
  color: #86868b;
  font-size: 13px;
  font-weight: 500;
}

.link-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.quick-link {
  padding: 10px 16px;
  background-color: #fff;
  border: 1px solid #e8e8e3;
  border-radius: 10px;
  color: #1d1d1f;
  font-size: 14px;
  text-decoration: none;
  transition: border-color 0.15s;
}

.quick-link:hover {
  border-color: #1d1d1f;
}

@media (max-width: 1080px) {
  .card-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 600px) {
  .home {
    padding: 24px 20px 32px;
  }

  .card-grid {
    grid-template-columns: 1fr;
  }
}
</style>
