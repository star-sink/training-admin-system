import request from "@/utils/request";

// 分页查询操作日志
export const queryPageApi = (page, pageSize) =>
  request.get("/log/page", { params: { page, pageSize } });
