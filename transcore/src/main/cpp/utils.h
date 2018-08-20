//
// Created by 袁锦凤 on 18/8/17.
//

#ifndef UDPDEMO_UTILS_H
#define UDPDEMO_UTILS_H

#include <string>
#include <vector>

using namespace std;

class Utils {
public:
    static vector<string> split(const string &str, const string &delim);

    static string getLocalIp(int sockfd);
};

#endif //UDPDEMO_UTILS_H
