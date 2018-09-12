/**********************************************************************
 * Trans db 操作的头文件
 * 
 * Author:  Ankie
 * Date:    2018/9/7
 **********************************************************************/

#ifndef LANTRANSAPP_TRANSDB_H
#define LANTRANSAPP_TRANSDB_H

#include <sys/types.h>

#define TRANS_TABLE_NAME "lanTransTbl"
#define TARNS_ID "trans_id"
#define TRANS_NAME "trans_name"
#define TRANS_ADDR "trans_addr"
#define TRANS_GROUP_ID "trans_group_id"

typedef struct stTransInfo {
    char trans_name[128];
    char trans_addr[128];
    int trans_group_id;
} TransInfo, *PTransInfo;

/**
 * 添加数据到表格中lanTransTbl，数据根据Name和组Id，不重复添加
 *
 * @param pTransInfo 数据
 * @return suc/fail
 */
int insertOneTransTblData(PTransInfo pTransInfo);

/**
 * 删除一条数据
 *
 * @param pData 数据
 * @return suc/fail
 */
int deleteOneTransData(PTransInfo pData);

/**
 * 删除Name下的数据
 *
 * @param pData 数据
 * @return suc/fail
 */
int deleteNameTransData(PTransInfo pData);

/**
 * 删除一组数据
 *
 * @param pData 数据
 * @return suc/fail
 */
int deleteOneGroupData(PTransInfo pData);

/**
 * 删除所有的数据
 *
 * @return suc/fail
 */
int deleteAllTransData();

/**
 * 更新表内所有Name下的Addr，更新之前检查表内是否存在该Name
 *
 * @param pData 要更新的数据
 * @return suc/fail
 */
int updateTransDataDeviceALLAddr(PTransInfo pData);

/**
 * 查询所有的数据
 *
 * @param ppStData 返回的数据指针
 * @param pNum 数量
 * @return suc/fail
 */
int queryAllTransTblData(PTransInfo *ppStData, int *pNum);

#endif //LANTRANSAPP_TRANSDB_H
