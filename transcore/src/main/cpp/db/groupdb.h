/**********************************************************************
 * Trans db 操作的头文件
 * 
 * Author:  Ankie
 * Date:    2018/9/7
 **********************************************************************/

#ifndef LANTRANSAPP_TRANSDB_H
#define LANTRANSAPP_TRANSDB_H

#include <sys/types.h>
#include "../const.h"

/**
 * 添加数据到表格中lanTransTbl，数据根据Name和组Id，不重复添加
 *
 * @param pTransInfo 数据
 * @return suc/fail
 */
int addGroupMemberData(PGroupInfo pTransInfo);

/**
 * 删除一条数据
 *
 * @param pData 数据
 * @return suc/fail
 */
int deleteGroupMemberData(PGroupInfo pTransInfo);

/**
 * 删除Name下的数据
 *
 * @param memberName 数据
 * @return suc/fail
 */
int deleteMemberDataByName(const char *memberName);

/**
 * 删除一组数据
 *
 * @param groupId 数据
 * @return suc/fail
 */
int deleteGroupDataById(int groupId);

/**
 * 删除所有的数据
 *
 * @return suc/fail
 */
int deleteAllGroupData();

/**
 * 更新表内所有Name下的Addr，更新之前检查表内是否存在该Name
 *
 * @param memberName 要更新的数据
 * @param newIp 新的地址
 * @return suc/fail
 */
int updateMemberDataByName(const char *memberName, const char *newIp);

/**
 * 查询所有的数据
 *
 * @param ppStData 返回的数据指针
 * @param pNum 数量
 * @return suc/fail
 */
int queryAllTransTblData(PGroupInfo *ppStData, int *pNum);

#endif //LANTRANSAPP_TRANSDB_H
