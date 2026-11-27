import request from "@/utils/request";

// 分页条件查询学员
export const queryPageApi = (clazzId, degree, name, page, pageSize) =>
  request.get("/students", {
    params: { clazzId, degree, name, page, pageSize },
  });

// 新增学员
export const addApi = (stu) => request.post("/students", stu);

// 根据 ID 查询学员
export const queryInfoApi = (id) => request.get(`/students/${id}`);

// 修改学员
export const updateApi = (stu) => request.put("/students", stu);

// 删除学员
export const deleteApi = (id) => request.delete(`/students/${id}`);

// 违纪处理
export const handleViolationApi = (id, score) =>
  request.put(`/students/violation/${id}/${score}`);
