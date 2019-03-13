package com.atomscat.modules.base.controller.manage;

import com.atomscat.common.utils.ResultUtil;
import com.atomscat.common.vo.Result;
import com.atomscat.modules.base.entity.Dict;
import com.atomscat.modules.base.service.DictDataService;
import com.atomscat.modules.base.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author Howell Yang
 */
@Slf4j
@RestController
@Api(description = "字典管理接口")
@RequestMapping("/rmp/dict")
@Transactional
public class DictController{

    @Autowired
    private DictService dictService;

    @Autowired
    private DictDataService dictDataService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping(value = "/getAll",method = RequestMethod.GET)
    @ApiOperation(value = "获取全部数据")
    public Result<List<Dict>> getAll(){

        List<Dict> list = dictService.findAllOrderBySortOrder();
        return new ResultUtil<List<Dict>>().setData(list);
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ApiOperation(value = "添加")
    public Result<Object> add(@ModelAttribute Dict dict){

        if(dictService.findByType(dict.getType())!=null){
            return new ResultUtil<Object>().setErrorMsg("字典类型Type已存在");
        }
        dictService.save(dict);
        return new ResultUtil<Object>().setSuccessMsg("添加成功");
    }

    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    @ApiOperation(value = "编辑")
    public Result<Object> edit(@ModelAttribute Dict dict){

        Dict old = dictService.get(dict.getId());
        // 若type修改判断唯一
        if(!old.getType().equals(dict.getType())&&dictService.findByType(dict.getType())!=null){
            return new ResultUtil<Object>().setErrorMsg("字典类型Type已存在");
        }
        dictService.update(dict);
        return new ResultUtil<Object>().setSuccessMsg("编辑成功");
    }

    @RequestMapping(value = "/delByIds/{id}",method = RequestMethod.DELETE)
    @ApiOperation(value = "通过id删除")
    public Result<Object> delAllByIds(@PathVariable String id){


        Dict dict = dictService.get(id);
        dictService.delete(id);
        dictDataService.deleteByDictId(id);
        // 删除缓存
        redisTemplate.delete("dictData::"+dict.getType());
        return new ResultUtil<Object>().setSuccessMsg("删除成功");
    }

    @RequestMapping(value = "/search",method = RequestMethod.GET)
    @ApiOperation(value = "搜索字典")
    public Result<List<Dict>> searchPermissionList(@RequestParam String key){

        List<Dict> list = dictService.findByTitleOrTypeLike(key);
        return new ResultUtil<List<Dict>>().setData(list);
    }
}
