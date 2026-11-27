import request from "@/utils/request";

// 分页条件查询员工
export const queryPageApi = (name, gender, begin, end, page, pageSize) =>
  request.get("/emps", {
    params: { name, gender, begin, end, page, pageSize },
  });

// 新增员工
export const addEmpApi = (data) => request.post("/emps", data);

// 根据 ID 查询员工
export const queryInfoApi = (id) => request.get(`/emps/${id}`);

// 修改员工
export const updateEmpApi = (data) => request.put("/emps", data);

// 批量删除员工
export const deleteEmpApi = (ids) =>
  request.delete("/emps", { params: { ids } });

// 查询全部员工
export const queryAllApi = () => request.get("/emps/list");
