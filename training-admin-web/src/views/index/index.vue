<script setup>

</script>

<template>
  <div class="home">
    <header class="home-header">
      <h1 class="home-title">教学后台</h1>
      <p class="home-subtitle">教学业务管理服务平台</p>
    </header>

    <section class="card-grid">
      <article class="info-card">
        <h2 class="info-card-title">考务管理</h2>
        <p class="info-card-desc">考试安排与成绩记录</p>
      </article>

      <article class="info-card">
        <h2 class="info-card-title">教务管理</h2>
        <p class="info-card-desc">班级、课程与教师安排</p>
      </article>

      <article class="info-card">
        <h2 class="info-card-title">教学数据</h2>
        <p class="info-card-desc">学员与班级数据概览</p>
      </article>

      <article class="info-card">
        <h2 class="info-card-title">反馈管理</h2>
        <p class="info-card-desc">学员反馈与问题跟进</p>
      </article>
    </section>

    <section class="activity">
      <h2 class="activity-title">最近动态</h2>
      <ul class="activity-list">
        <li class="activity-item">
          <time class="activity-time">09-12 14:30</time>
          <span class="activity-text">新增学员「陈晨」，分配至 Java 就业班</span>
        </li>
        <li class="activity-item">
          <time class="activity-time">09-12 10:15</time>
          <span class="activity-text">班级「前端就业 03 期」完成结课</span>
        </li>
        <li class="activity-item">
          <time class="activity-time">09-11 17:40</time>
          <span class="activity-text">更新员工「李明」的部门与职位</span>
        </li>
        <li class="activity-item">
          <time class="activity-time">09-11 09:05</time>
          <span class="activity-text">处理一条学员违纪记录</span>
        </li>
        <li class="activity-item">
          <time class="activity-time">09-10 16:20</time>
          <span class="activity-text">新增部门「教学研发部」</span>
        </li>
      </ul>
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
  padding: 20px;
  background-color: #fff;
  border: 1px solid #e8e8e3;
  border-radius: 14px;
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

.activity {
  margin-top: 40px;
}

.activity-title {
  margin: 0 0 4px;
  color: #86868b;
  font-size: 13px;
  font-weight: 500;
}

.activity-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.activity-item {
  display: flex;
  align-items: baseline;
  gap: 24px;
  padding: 14px 0;
  border-bottom: 1px solid rgb(29 29 31 / 8%);
  font-size: 14px;
}

.activity-item:last-child {
  border-bottom: 0;
}

.activity-time {
  flex: 0 0 104px;
  color: #86868b;
  font-size: 13px;
  font-variant-numeric: tabular-nums;
}

.activity-text {
  color: #1d1d1f;
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

  .activity-item {
    flex-direction: column;
    gap: 4px;
  }
}
</style>
