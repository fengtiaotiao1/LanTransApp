/**********************************************************************
 * Common 变量
 * 
 * Author:  Ankie
 * Date:    2018/9/7
 **********************************************************************/

#ifndef LANTRANSAPP_TBLSQLCONST_H
#define LANTRANSAPP_TBLSQLCONST_H

#define DB_SUCCESS 0
#define DB_FAILED 1

#define DB_VERSION 1

#define DB_SQL_STR_MAX_LEN 2048

#define DEFAULT_SAVE_PATH "/mnt/internal_sd/download/files/"


#define CREATE_LAN_TRANS_TABLE_STR (\
"create table if not exists lanTransTbl\
(\
trans_id INTEGER PRIMARY KEY AUTOINCREMENT,\
trans_name VARCHAR(128) NOT NULL,\
trans_addr VARCHAR(128) NOT NULL,\
trans_group_id INTEGER\
);")

#endif //LANTRANSAPP_TBLSQLCONST_H
