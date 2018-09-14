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
#include "groupdb.h"
#include "tblsqlconst.h"
#include "sqlitehelp.h"
#include "../log.h"

extern sqlite3 *g_pDatabase;

static int insertGroupTblData(PGroupInfo pTransInfo);

static int deleteGroupTblData(char *pRegular);

static int updateMemberIpDataByName(const char *memberName, const char *newIp);

static int queryGroupMemberData(PGroupInfo pData, int *Num);

static int queryMemberExist(const char *memberName, int *Num);

static int queryGroupTblExist(int groupId, int *Num);


// 添加数据到表格中lanTransTbl，数据根据Name和组Id，不重复添加
int addGroupMemberData(PGroupInfo pTransInfo) {
    int iNum = 0;
    int ret = queryGroupMemberData(pTransInfo, &iNum);
    if (DB_SUCCESS == ret) {
        if (0 == iNum) {
            insertGroupTblData(pTransInfo);
        } else if (iNum > 0) {
            LOGE("insert database table has this data");
        }
    }

    return DB_FAILED;
}

// 删除一个数据
int deleteGroupMemberData(PGroupInfo pData) {
    int iNum = 0;
    int iQRet = queryGroupMemberData(pData, &iNum);
    if (DB_SUCCESS != iQRet || iNum == 0) {
        LOGE("deleteGroupMemberData query trans tbl iNum is : %d", iNum);
        return DB_FAILED;
    }

    LOGD("deleteGroupMemberData query trans tbl iNum is : %d", iNum);
    char regular[1024] = {'\0'};
    // 拼接sql语句
    sprintf(regular + strlen(regular), "%s = \'%s\' and ", GROUP_MEMBER_NAME, pData->member_name);
    sprintf(regular + strlen(regular), "%s = %d ", GROUP_ID, pData->group_id);
    return deleteGroupTblData(regular);
}

// 删除Name下的数据
int deleteMemberDataByName(const char *memberName) {
    int iNum = 0;
    int iQRet = queryMemberExist(memberName, &iNum);
    if (DB_SUCCESS != iQRet || iNum == 0) {
        LOGE("deleteGroupMemberData query trans tbl iNum is : %d", iNum);
        return DB_FAILED;
    }

    LOGD("deleteMemberDataByName query trans tbl iNum is : %d", iNum);
    char regular[1024] = {'\0'};
    // 拼接sql语句
    sprintf(regular + strlen(regular), "%s = \'%s\'", GROUP_MEMBER_NAME, memberName);
    return deleteGroupTblData(regular);
}

// 删除一个组的数据
int deleteGroupDataById(int groupId) {
    int iNum = 0;
    int iQRet = queryGroupTblExist(groupId, &iNum);
    if (DB_SUCCESS != iQRet || iNum == 0) {
        LOGE("deleteGroupDataById query trans tbl iNum is : %d", iNum);
        return DB_FAILED;
    }

    LOGD("deleteGroupDataById query trans tbl iNum is : %d", iNum);
    char regular[1024] = {'\0'};
    // 拼接sql语句
    sprintf(regular + strlen(regular), "%s = %d ", GROUP_ID, groupId);
    return deleteGroupTblData(regular);
}

// 直接删除所有的数据
int deleteAllGroupData() {
    return deleteGroupTblData(NULL);
}

// 更新表内所有Name下的Addr，更新之前检查表内是否存在该Name
int updateMemberDataByName(const char *memberName, const char *newIp) {
    int iNum = 0;
    int ret = queryMemberExist(memberName, &iNum);
    if (DB_SUCCESS == ret) {
        if (iNum > 0) {
            updateMemberIpDataByName(memberName, newIp);
        } else if (0 == iNum) {
            LOGE("update database table name num is 0!");
        }
    }
    return DB_FAILED;
}


// 查询所有的数据，注意在调用该方法之后插入链表之后释放内存
int queryAllTransTblData(PGroupInfo *ppStData, int *pNum) {
    if (NULL == ppStData) {
        return DB_FAILED;
    }

    char sqlBuf[2048] = {'\0'};
    sprintf(sqlBuf, "select * from %s", GROUP_TABLE_NAME);

    char **dbResult;
    int nRow, nColumn, index;

    int iQRet = execQuerySql(g_pDatabase, sqlBuf, &dbResult, &nRow, &nColumn);

    *ppStData = (PGroupInfo) malloc(sizeof(GroupInfo) * nRow);
    *pNum = nRow;

    if (DB_SUCCESS == iQRet) {
        index = nColumn;
        for (int i = 0; i < nRow; i++) {
            for (int j = 0; j < nColumn; j++) {
                if (!strcmp(dbResult[j], GROUP_MEMBER_ADDR)) {
                    if (dbResult[index]) {
                        strcpy((*ppStData)[i].member_addr, dbResult[index]);
                    }
                } else if (!strcmp(dbResult[j], GROUP_MEMBER_NAME)) {
                    if (dbResult[index]) {
                        strcpy((*ppStData)[i].member_name, dbResult[index]);
                    }
                } else if (!strcmp(dbResult[j], GROUP_ID)) {
                    if (dbResult[index]) {
                        (*ppStData)[i].group_id = atoi(dbResult[index]);
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
static int insertGroupTblData(PGroupInfo pTransInfo) {
    char sqlBuf[2048] = {'\0'};
    char keysSet[1024] = {'\0'};
    char regular[1024] = {'\0'};

    executeTransactionBegin(g_pDatabase);

    // 拼接keysSet语句
    sprintf(keysSet + strlen(keysSet), "%s, ", GROUP_MEMBER_ADDR);
    sprintf(keysSet + strlen(keysSet), "%s, ", GROUP_MEMBER_NAME);
    sprintf(keysSet + strlen(keysSet), "%s", GROUP_ID);
    // 拼接regular语句
    sprintf(regular + strlen(regular), "\'%s\', ", pTransInfo->member_addr);
    sprintf(regular + strlen(regular), "\'%s\', ", pTransInfo->member_name);
    sprintf(regular + strlen(regular), "%d", pTransInfo->group_id);
    // 拼接sql语句
    sprintf(sqlBuf, "insert into %s (%s) values (%s)", GROUP_TABLE_NAME, keysSet, regular);

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
static int deleteGroupTblData(char *pRegular) {
    char sqlBuf[DB_SQL_STR_MAX_LEN] = {'\0'};

    executeTransactionBegin(g_pDatabase);

    if (NULL == pRegular) {
        sprintf(sqlBuf, "delete from %s", GROUP_TABLE_NAME);
    } else {
        sprintf(sqlBuf, "delete from %s where %s", GROUP_TABLE_NAME, pRegular);
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
 * @param pData PGroupInfo
 * @return 更新成功失败
 */
static int updateMemberIpDataByName(const char *memberName, const char *newIp) {
    char sqlBuf[2048] = {'\0'};
    char regular[1024] = {'\0'};
    char variate[1024] = {'\0'};

    executeTransactionBegin(g_pDatabase);

    // 拼接regular数据/variate数据
    sprintf(regular + strlen(regular), "%s = \'%s\'", GROUP_MEMBER_ADDR, newIp);
    sprintf(variate + strlen(variate), "%s = \'%s\'", GROUP_MEMBER_NAME, memberName);
    // 拼接sql数据
    sprintf(sqlBuf, "update %s set %s where %s", GROUP_TABLE_NAME, regular, variate);

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
 * @param pData PGroupInfo
 * @param Num 数据条数
 * @return suc/fail
 */
static int queryGroupMemberData(PGroupInfo pData, int *Num) {

    char sqlBuf[2048] = {'\0'};
    char regular[1024] = {'\0'};
    // 拼接查询语句
    sprintf(regular + strlen(regular), "%s = \'%s\' and ", GROUP_MEMBER_NAME, pData->member_name);
    sprintf(regular + strlen(regular), "%s = %d", GROUP_ID, pData->group_id);
    sprintf(sqlBuf, "select * from %s where %s", GROUP_TABLE_NAME, regular);

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
static int queryMemberExist(const char *memberName, int *Num) {

    char sqlBuf[2048] = {'\0'};
    char regular[1024] = {'\0'};
    // 拼接查询语句
    sprintf(regular + strlen(regular), "%s = \'%s\'", GROUP_MEMBER_NAME, memberName);
    sprintf(sqlBuf, "select * from %s where %s", GROUP_TABLE_NAME, regular);

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
static int queryGroupTblExist(int groupId, int *Num) {
    char sqlBuf[2048] = {'\0'};
    char regular[1024] = {'\0'};
    // 拼接查询语句
    sprintf(regular + strlen(regular), "%s = %d", GROUP_ID, groupId);
    sprintf(sqlBuf, "select * from %s where %s", GROUP_TABLE_NAME, regular);

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