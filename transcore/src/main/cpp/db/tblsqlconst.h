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

#define GROUP_TABLE_NAME "groupTbl"
#define KEY_ID "id"
#define GROUP_MEMBER_NAME "group_member_name"
#define GROUP_MEMBER_ADDR "group_member_addr"
#define GROUP_ID "group_id"


#define CREATE_LAN_GROUP_TABLE_STR (\
"create table if not exists groupTbl\
(\
id INTEGER PRIMARY KEY AUTOINCREMENT,\
group_member_name VARCHAR(128) NOT NULL,\
group_member_addr VARCHAR(128) NOT NULL,\
group_id INTEGER\
);")

#endif //LANTRANSAPP_TBLSQLCONST_H
