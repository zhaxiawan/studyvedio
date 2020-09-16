package com.lagou.edu.dao;

import com.lagou.edu.annotation.Repository;
import com.lagou.edu.pojo.Account;

/**
 * @author 应癫
 */

@Repository(id="accountDao",isInterface = false)
public interface AccountDao {

    Account queryAccountByCardNo(String cardNo) throws Exception;

    int updateAccountByCardNo(Account account) throws Exception;
}
