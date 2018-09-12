/**********************************************************************
 * sqlite 接口头文件
 * 
 * Author:  Ankie
 * Date:    2018/9/7
 **********************************************************************/

#ifndef LANTRANSAPP_SQLITEHELP_H
#define LANTRANSAPP_SQLITEHELP_H

#include "sqlite3.h"

extern sqlite3 *g_pDatabase;

/**
 * create or update struct
 */
typedef struct stDBFuncCreateOrUpdate {
    // create db
    int (*createTable)(sqlite3 *db);

    // update db version
    int (*updateDatabase)(sqlite3 *db, int oldVersion, int newVersion);
} stDBFuncCreateOrUpdate;


/**
 * 真正对外的DB接口
 *
 * @param strDbName DB名称
 * @return          suc/failure
 */
int openLanTransDb(const char *strDbName);


/**
 * open db
 *
 * @param strDbName     db name
 * @param ppDatebase    db pointer
 * @param func          pointer
 * @param iVersion      version
 * @return              suc/failure
 */
int openDatabase(const char *strDbName, sqlite3 **ppDatebase, stDBFuncCreateOrUpdate *func,
                 int iVersion);

/**
 * create table
 *
 * @param pDatabase     db pointer
 * @param strSql        sql string
 * @return              suc/failure
 */
int createTable(sqlite3 *pDatabase, char *strSql);

/**
 * close db
 *
 * @param pDatabase     db pointer
 * @return              suc/failure
 */
int closeDatabase(sqlite3 *pDatabase);

/**
 * exec string sql
 *
 * @param pDatabase     db
 * @param strSql        db string
 * @return              suc/failure
 */
int execSqlString(sqlite3 *pDatabase, char *strSql);

/**
 * exec Query sql
 *
 * @param pDatabase     db pointer
 * @param strSql        sql string
 * @param strResult     ret
 * @param iRow          row num
 * @param iColumn       col num
 * @return              suc/fail
 */
int execQuerySql(sqlite3 *pDatabase, char *strSql, char ***strResult, int *iRow, int *iColumn);


/**
 * free table
 *
 * @param strRequest str
 */
void freeTable(char **strRequest);

/**
 * 开始
 *
 * @param hDbCon g_pDatabase
 */
void executeTransactionBegin(sqlite3 *hDbCon);

/**
 * 回滚
 *
 * @param hDbCon g_pDatabase
 */
void executeTransactionRollback(sqlite3 *hDbCon);

/**
 * 结束
 *
 * @param hDbCon g_pDatabase
 */
void executeTransactionEnd(sqlite3 *hDbCon);

#endif //LANTRANSAPP_SQLITEHELP_H
