package com.atomscat.modules.base.controller.manage;

import com.atomscat.common.utils.PageUtil;
import com.atomscat.common.utils.ResultUtil;
import com.atomscat.common.vo.PageVo;
import com.atomscat.common.vo.Result;
import com.atomscat.modules.base.entity.Dict;
import com.atomscat.modules.base.entity.DictData;
import com.atomscat.modules.base.service.DictDataService;
import com.atomscat.modules.base.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author Howell Yang
 */
@Slf4j
@RestController
@Api(description = "字典数据管理接口")
@RequestMapping("/rmp/dictData")
@CacheConfig(cacheNames = "dictData")
@Transactional
public class DictDataController{

    @Autowired
    private DictService dictService;

    @Autowired
    private DictDataService dictDataService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping(value = "/getByCondition",method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取用户列表")
    public Result<Page<DictData>> getByCondition(@ModelAttribute DictData dictData,
                                                 @ModelAttribute PageVo pageVo){

        Page<DictData> page = dictDataService.findByCondition(dictData, PageUtil.initPage(pageVo));
        return new ResultUtil<Page<DictData>>().setData(page);
    }

    @RequestMapping(value = "/getByType/{type}",method = RequestMethod.GET)
    @ApiOperation(value = "通过类型获取")
    @Cacheable(key = "#type")
    public Result<Object> getByType(@PathVariable String type){

        Dict dict = dictService.findByType(type);
        if (dict == null) {
            return new ResultUtil<Object>().setErrorMsg("字典类型Type不存在");
        }
        List<DictData> list = dictDataService.findByDictId(dict.getId());
        return new ResultUtil<Object>().setData(list);
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ApiOperation(value = "添加")
    public Result<Object> add(@ModelAttribute DictData dictData){

        Dict dict = dictService.get(dictData.getDictId());
        if (dict == null) {
            return new ResultUtil<Object>().setErrorMsg("字典类型id不存在");
        }
        dictDataService.save(dictData);
        // 删除缓存
        redisTemplate.delete("dictData::"+dict.getType());
        return new ResultUtil<Object>().setSuccessMsg("添加成功");
    }

    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    @ApiOperation(value = "编辑")
    public Result<Object> edit(@ModelAttribute DictData dictData){

        dictDataService.update(dictData);
        // 删除缓存
        Dict dict = dictService.get(dictData.getDictId());
        redisTemplate.delete("dictData::"+dict.getType());
        return new ResultUtil<Object>().setSuccessMsg("编辑成功");
    }

    @RequestMapping(value = "/delByIds/{ids}",method = RequestMethod.DELETE)
    @ApiOperation(value = "批量通过id删除")
    public Result<Object> delByIds(@PathVariable String[] ids){

        for(String id : ids){
            DictData dictData = dictDataService.get(id);
            Dict dict = dictService.get(dictData.getDictId());
            dictDataService.delete(id);
            // 删除缓存
            redisTemplate.delete("dictData::"+dict.getType());
        }
        return new ResultUtil<Object>().setSuccessMsg("批量通过id删除数据成功");
    }
}
