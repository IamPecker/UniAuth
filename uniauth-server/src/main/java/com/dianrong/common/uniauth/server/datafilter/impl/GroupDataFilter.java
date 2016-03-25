package com.dianrong.common.uniauth.server.datafilter.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianrong.common.uniauth.common.bean.InfoName;
import com.dianrong.common.uniauth.server.data.entity.Grp;
import com.dianrong.common.uniauth.server.datafilter.FieldType;
import com.dianrong.common.uniauth.server.exp.AppException;
import com.dianrong.common.uniauth.server.service.GroupService;
import com.dianrong.common.uniauth.server.util.TypeParseUtil;
import com.dianrong.common.uniauth.server.util.UniBundle;

/**.
 * 组的数据过滤处理实现.
 * @author wanglin
 */
@Service("groupDataFilter")
public class GroupDataFilter extends CurrentAbastracDataFIleter{
	
	@Autowired
	private GroupService groupService;
	
	/**.
	 * 标示处理的表名
	 */
	private String processTalbeName = "组数据";
	
	/**.
	 * 处理过滤status=0的情况
	 * @param filterMap 过滤条件字段
	 */
	@Override
	public void filterStatusEqual0(Map<FieldType, Object> filterMap){
		Set<Entry<FieldType, Object>> entrySet = filterMap.entrySet();
		//遍历
		for(Entry<FieldType, Object> kv : entrySet){
			switch(kv.getKey()){
			case FIELD_TYPE_ID:
				int countById = groupService.countGroupByIdWithStatusEffective(TypeParseUtil.paraseToLongFromObject(kv.getValue()));
				//有数据  就要报错
				if(countById > 0){
					throw new AppException(InfoName.INTERNAL_ERROR, UniBundle.getMsg("datafilter.data.exsit.error", processTalbeName , "id" , TypeParseUtil.paraseToLongFromObject(kv.getValue())));
				}
				break;
			case FIELD_TYPE_CODE:
				int countByCode = groupService.countGroupByCodeWithStatusEffective(TypeParseUtil.paraseToStringFromObject(kv.getValue()));
				if(countByCode > 0){
					throw new AppException(InfoName.INTERNAL_ERROR, UniBundle.getMsg("datafilter.data.exsit.error", processTalbeName , "code" , TypeParseUtil.paraseToStringFromObject(kv.getValue())));
				}
				break;
			default:
				break;
			}
		}
	}
	
	/**.
	 * 处理过滤不能出现status=0的情况
	 * @param filterMap 入参数据
	 */
	@Override
	public void filterNoStatusEqual0(Map<FieldType, Object> filterMap){
		Set<Entry<FieldType, Object>> entrySet = filterMap.entrySet();
		//遍历
		for(Entry<FieldType, Object> kv : entrySet){
			switch(kv.getKey()){
			case FIELD_TYPE_ID:
				int countById = groupService.countGroupByIdWithStatusEffective(TypeParseUtil.paraseToLongFromObject(kv.getValue()));
				//有数据  就要报错
				if(countById <= 0){
					throw new AppException(InfoName.INTERNAL_ERROR, UniBundle.getMsg("datafilter.data.notexsit.error", "grp" , "id" , TypeParseUtil.paraseToLongFromObject(kv.getValue())));
				}
				break;
			case FIELD_TYPE_CODE:
				int countByCode = groupService.countGroupByCodeWithStatusEffective(TypeParseUtil.paraseToStringFromObject(kv.getValue()));
				if(countByCode <= 0){
					throw new AppException(InfoName.INTERNAL_ERROR, UniBundle.getMsg("datafilter.data.notexsit.error", "grp" , "code" , TypeParseUtil.paraseToStringFromObject(kv.getValue())));
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void doFileterFieldValueIsExsist(FieldType type, Integer id, Object fieldValue) {
		switch(type){
			case FIELD_TYPE_CODE:
					String newCode = TypeParseUtil.paraseToStringFromObject(fieldValue);
					Grp grpInfo = groupService.selectByIdWithStatusEffective(id);
					if(grpInfo != null){
						//如果数据信息没有改变  则不管
						if(newCode.equals(grpInfo.getCode())){
							break;
						}
					} 
					//查看是否存在其他的记录是该code
					Map<FieldType, Object> tmap = new HashMap<FieldType, Object>();
					tmap.put(FieldType.FIELD_TYPE_CODE, newCode);
					//进行判断
					this.filterStatusEqual0(tmap);
				break;
			default:
				break;
		}
	}
}
