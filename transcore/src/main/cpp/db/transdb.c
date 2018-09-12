/**********************************************************************
 * Trans db 操作
 * 
 * Author:  Ankie
 * Date:    2018/9/7
 **********************************************************************/

#include <stdio.h>
#include <string.h>
#include <malloc.h>
#include <stdlib.h>
#include "sqlite3.h"
#include "transdb.h"
#include "tblsqlconst.h"
#include "sqlitehelp.h"
#include "../log.h"

extern sqlite3 *g_pDatabase;

static int insertTransTblData(PTransInfo pTransInfo);

static int deleteTransTblData(char *pRegular);

static int updateTransDataDeviceAddr(PTransInfo pData);

static int queryOneTransTblData(PTransInfo pData, int *Num);

static int queryTransTblNameExist(PTransInfo pData, int *Num);

static int queryTransTblGroupExist(PTransInfo pData, int *Num);


// 添加数据到表格中lanTransTbl，数据根据Name和组Id，不重复添加
int insertOneTransTblData(PTransInfo pTransInfo) {
    int iNum = 0;
    int ret = queryOneTransTblData(pTransInfo, &iNum);
    if (DB_SUCCESS == ret) {
        if (0 == iNum) {
            insertTransTblData(pTransInfo);
        } else if (iNum > 0) {
            LOGE("insert database table has this data");
        }
    }

    return DB_FAILED;
}

// 删除一个数据
int deleteOneTransData(PTransInfo pData) {
    int iNum = 0;
    int iQRet = queryOneTransTblData(pData, &iNum);
    if (DB_SUCCESS != iQRet || iNum == 0) {
        LOGE("deleteOneTransData query trans tbl iNum is : %d", iNum);
        return DB_FAILED;
    }

    LOGD("deleteOneTransData query trans tbl iNum is : %d", iNum);
    char regular[1024] = {'\0'};
    // 拼接sql语句
    sprintf(regular + strlen(regular), "%s = \'%s\' and ", TRANS_NAME, pData->trans_name);
    sprintf(regular + strlen(regular), "%s = %d ", TRANS_GROUP_ID, pData->trans_group_id);
    return deleteTransTblData(regular);
}

// 删除Name下的数据
int deleteNameTransData(PTransInfo pData) {
    int iNum = 0;
    int iQRet = queryTransTblNameExist(pData, &iNum);
    if (DB_SUCCESS != iQRet || iNum == 0) {
        LOGE("deleteOneTransData query trans tbl iNum is : %d", iNum);
        return DB_FAILED;
    }

    LOGD("deleteNameTransData query trans tbl iNum is : %d", iNum);
    char regular[1024] = {'\0'};
    // 拼接sql语句
    sprintf(regular + strlen(regular), "%s = \'%s\'", TRANS_NAME, pData->trans_name);
    return deleteTransTblData(regular);
}

// 删除一个组的数据
int deleteOneGroupData(PTransInfo pData) {
    int iNum = 0;
    int iQRet = queryTransTblGroupExist(pData, &iNum);
    if (DB_SUCCESS != iQRet || iNum == 0) {
        LOGE("deleteOneGroupData query trans tbl iNum is : %d", iNum);
        return DB_FAILED;
    }

    LOGD("deleteOneGroupData query trans tbl iNum is : %d", iNum);
    char regular[1024] = {'\0'};
    // 拼接sql语句
    sprintf(regular + strlen(regular), "%s = %d ", TRANS_GROUP_ID, pData->trans_group_id);
    return deleteTransTblData(regular);
}

// 直接删除所有的数据
int deleteAllTransData() {
    return deleteTransTblData(NULL);
}

// 更新表内所有Name下的Addr，更新之前检查表内是否存在该Name
int updateTransDataDeviceALLAddr(PTransInfo pTransInfo) {
    int iNum = 0;
    int ret = queryTransTblNameExist(pTransInfo, &iNum);
    if (DB_SUCCESS == ret) {
        if (iNum > 0) {
            updateTransDataDeviceAddr(pTransInfo);
        } else if (0 == iNum) {
            LOGE("update database table name num is 0!");
        }
    }
    return DB_FAILED;
}


// 查询所有的数据，注意在调用该方法之后插入链表之后释放内存
int queryAllTransTblData(PTransInfo *ppStData, int *pNum) {
    if (NULL == ppStData) {
        return DB_FAILED;
    }

    char sqlBuf[2048] = {'\0'};
    sprintf(sqlBuf, "select * from %s", TRANS_TABLE_NAME);

    char **dbResult;
    int nRow, nColumn, index;

    int iQRet = execQuerySql(g_pDatabase, sqlBuf, &dbResult, &nRow, &nColumn);

    *ppStData = (PTransInfo) malloc(sizeof(TransInfo) * nRow);
    *pNum = nRow;

    if (DB_SUCCESS == iQRet) {
        index = nColumn;
        for (int i = 0; i < nRow; i++) {
            for (int j = 0; j < nColumn; j++) {
                if (!strcmp(dbResult[j], TRANS_ADDR)) {
                    if (dbResult[index]) {
                        strcpy((*ppStData)[i].trans_addr, dbResult[index]);
                    }
                } else if (!strcmp(dbResult[j], TRANS_NAME)) {
                    if (dbResult[index]) {
                        strcpy((*ppStData)[i].trans_name, dbResult[index]);
                    }
                } else if (!strcmp(dbResult[j], TRANS_GROUP_ID)) {
                    if (dbResult[index]) {
                        (*ppStData)[i].trans_group_id = atoi(dbResult[index]);
                    }
                }
                ++index;
            }
        }
    }
    freeTable(dbResult);
    return iQRet;
}


/**
 * 插入数据
 *
 * @param pTransInfo 数据
 * @return suc/fail
 */
static int insertTransTblData(PTransInfo pTransInfo) {
    char sqlBuf[2048] = {'\0'};
    char keysSet[1024] = {'\0'};
    char regular[1024] = {'\0'};

    executeTransactionBegin(g_pDatabase);

    // 拼接keysSet语句
    sprintf(keysSet + strlen(keysSet), "%s, ", TRANS_ADDR);
    sprintf(keysSet + strlen(keysSet), "%s, ", TRANS_NAME);
    sprintf(keysSet + strlen(keysSet), "%s", TRANS_GROUP_ID);
    // 拼接regular语句
    sprintf(regular + strlen(regular), "\'%s\', ", pTransInfo->trans_addr);
    sprintf(regular + strlen(regular), "\'%s\', ", pTransInfo->trans_name);
    sprintf(regular + strlen(regular), "%d", pTransInfo->trans_group_id);
    // 拼接sql语句
    sprintf(sqlBuf, "insert into %s (%s) values (%s)", TRANS_TABLE_NAME, keysSet, regular);

    LOGD("==db==, %s", sqlBuf);
    int iSqlRet = execSqlString(g_pDatabase, sqlBuf);

    if (DB_FAILED == iSqlRet) {
        executeTransactionRollback(g_pDatabase);
    } else {
        executeTransactionEnd(g_pDatabase);
    }

    return iSqlRet;
}


/**
 * 删除表格中的数据
 *
 * @param pRegular 删除数据，全部删除/指定删除
 * @return 删除是否成功
 */
static int deleteTransTblData(char *pRegular) {
    char sqlBuf[DB_SQL_STR_MAX_LEN] = {'\0'};

    executeTransactionBegin(g_pDatabase);

    if (NULL == pRegular) {
        sprintf(sqlBuf, "delete from %s", TRANS_TABLE_NAME);
    } else {
        sprintf(sqlBuf, "delete from %s where %s", TRANS_TABLE_NAME, pRegular);
    }
    int iSqlRet = execSqlString(g_pDatabase, sqlBuf);

    if (DB_FAILED == iSqlRet) {
        executeTransactionRollback(g_pDatabase);
    } else {
        executeTransactionEnd(g_pDatabase);
    }

    return iSqlRet;
}

/**
 * 更新trans_name下的IP地址数据
 *
 * @param pData PTransInfo
 * @return 更新成功失败
 */
static int updateTransDataDeviceAddr(PTransInfo pData) {
    char sqlBuf[2048] = {'\0'};
    char regular[1024] = {'\0'};
    char variate[1024] = {'\0'};

    executeTransactionBegin(g_pDatabase);

    // 拼接regular数据/variate数据
    sprintf(regular + strlen(regular), "%s = \'%s\'", TRANS_ADDR, pData->trans_addr);
    sprintf(variate + strlen(variate), "%s = \'%s\'", TRANS_NAME, pData->trans_name);
    // 拼接sql数据
    sprintf(sqlBuf, "update %s set %s where %s", TRANS_TABLE_NAME, regular, variate);

    int iSqlRet = execSqlString(g_pDatabase, sqlBuf);

    if (DB_FAILED == iSqlRet) {
        executeTransactionRollback(g_pDatabase);
    } else {
        executeTransactionEnd(g_pDatabase);
    }

    return iSqlRet;
}

/**
 * 查询表内是否存在一条数据
 *
 * @param pData PTransInfo
 * @param Num 数据条数
 * @return suc/fail
 */
static int queryOneTransTblData(PTransInfo pData, int *Num) {

    char sqlBuf[2048] = {'\0'};
    char regular[1024] = {'\0'};
    // 拼接查询语句
    sprintf(regular + strlen(regular), "%s = \'%s\' and ", TRANS_NAME, pData->trans_name);
    sprintf(regular + strlen(regular), "%s = %d", TRANS_GROUP_ID, pData->trans_group_id);
    sprintf(sqlBuf, "select * from %s where %s", TRANS_TABLE_NAME, regular);

    char **dbResult;
    int nRow, nColumn;
    int iQRet = execQuerySql(g_pDatabase, sqlBuf, &dbResult, &nRow, &nColumn);
    LOGD("nRow is :%d", nRow);
    if (DB_SUCCESS == iQRet) {
        *Num = nRow;
        freeTable(dbResult);
        return iQRet;
    }
    freeTable(dbResult);
    return iQRet;
}

/**
 * 查询表中是否存在该Name
 * @param pData  条件数据
 * @param Num  数量
 * @return suc/fail
 */
static int queryTransTblNameExist(PTransInfo pData, int *Num) {

    char sqlBuf[2048] = {'\0'};
    char regular[1024] = {'\0'};
    // 拼接查询语句
    sprintf(regular + strlen(regular), "%s = \'%s\'", TRANS_NAME, pData->trans_name);
    sprintf(sqlBuf, "select * from %s where %s", TRANS_TABLE_NAME, regular);

    char **dbResult;
    int nRow, nColumn;
    int iQRet = execQuerySql(g_pDatabase, sqlBuf, &dbResult, &nRow, &nColumn);
    LOGD("nRow is :%d", nRow);
    if (DB_SUCCESS == iQRet) {
        *Num = nRow;
        freeTable(dbResult);
        return iQRet;
    }
    freeTable(dbResult);
    return iQRet;
}

/**
 * 查询组是否存在
 *
 * @param pData 数据
 * @param Num 返回组的数量
 * @return suc/fail
 */
static int queryTransTblGroupExist(PTransInfo pData, int *Num) {
    char sqlBuf[2048] = {'\0'};
    char regular[1024] = {'\0'};
    // 拼接查询语句
    sprintf(regular + strlen(regular), "%s = %d", TRANS_GROUP_ID, pData->trans_group_id);
    sprintf(sqlBuf, "select * from %s where %s", TRANS_TABLE_NAME, regular);

    char **dbResult;
    int nRow, nColumn;
    int iQRet = execQuerySql(g_pDatabase, sqlBuf, &dbResult, &nRow, &nColumn);
    LOGD("nRow is :%d", nRow);
    if (DB_SUCCESS == iQRet) {
        *Num = nRow;
        freeTable(dbResult);
        return iQRet;
    }
    freeTable(dbResult);
    return iQRet;
}