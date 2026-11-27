package com.trainingadmin.controller;

import com.trainingadmin.anno.Log;
import com.trainingadmin.pojo.Dept;
import com.trainingadmin.pojo.Result;
import com.trainingadmin.service.DeptService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/depts")//设置公共路径
@RestController
public class DeptController {


  //  private static final Logger log = LoggerFactory.getLogger(DeptController.class);//固定的


    @Autowired
    private DeptService deptService;


    //@RequestMapping(value = "/depts",method = RequestMethod.GET)//method:指定请求方式
    @GetMapping
    //@GetMapping("/depts")
    public Result list(){
        System.out.println("查询全部的部门数据");
        List <Dept> deptList =deptService.findAll();
        return  Result.success(deptList);
    }

    /**
     *删除部门  方法一：HttpServletRequest 获取请求参数
     */
//    @DeleteMapping("/depts")
//    public Result delete(HttpServletRequest request){
//            String idStr=request.getParameter("id");
//            Integer id=Integer.parseInt(idStr);
//            System.out.println("根据id删除部门"+id);
//            return Result.success();
//    }
//}
    /**
     *删除部门  方法二：@RequestParam
     * 注意事项：一旦声明@RequestParam，该参数在请求时必须传递，如果不传递报错，（默认required为true）
     */
//    @DeleteMapping("/depts")
//    public Result delete(@RequestParam(value = "id",required = false)Integer deptId){
//            System.out.println("根据id删除部门"+deptId);
//            return Result.success();
//    }
//}

/**
 *删除部门  方法三：省略@RequestParam（前端传递的请求参数名与服务端方法形参名一致）[推荐]
 *
 */
@Log
@DeleteMapping
//@DeleteMapping("/depts")
public Result delete(Integer id){
    log.info("根据id删除部门:{}",id);
    deptService.deleteById(id);
    return Result.success();
    }

    /**
     *新增部门
     *
     */
    @Log
    @PostMapping
//@PostMapping("/depts")
    public Result add(@RequestBody Dept dept){
    deptService.add(dept);
   // System.out.println("添加部门"+dept);
    log.info("添加部门{}",dept);
    return  Result.success();
    }

    /**
     * 修改部门
     */
//    @GetMapping("/depts/{id}")     // 路径参数版,如 /depts/3
//    public Result getById(@PathVariable("id")Integer deptId){
//        System.out.println("根据ID查询部门"+deptId);
//        return Result.success();
//    }
    /**
     * 修改部门(查询回显）
     */
    @GetMapping("/{id:\\d+}")
    //@GetMapping("/depts/{id}")     // 路径参数版,如 /depts/3
    public Result getById(@PathVariable Integer id){
        System.out.println("根据ID查询部门"+id);
        Dept dept=deptService.getById(id);
        return Result.success(dept);
    }

    /**
     * 修改部门(修改数据）
     */
    @Log
    @PutMapping
    //@PutMapping("/depts")
    public Result update(@RequestBody Dept dept){
        System.out.println("修改部门数据"+dept);
        deptService.update(dept);
        return Result.success();
    }
}
