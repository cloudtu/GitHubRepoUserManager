package cloudtu.github;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;

import cloudtu.util.GitHubUtil;
import cloudtu.util.SysConfig;

public class RepoUserImporter {
	private static final Logger logger = Logger.getLogger(RepoUserImporter.class);
	private static GitHubUtil gitHubUtil = new GitHubUtil(SysConfig.getString("AdminLoginId"), SysConfig.getString("AdminLoginPwd"));
	private static ResourceBundle resourceBundle = ResourceBundle.getBundle("github_repo");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		try {			
			logger.info(RepoUserImporter.class.getSimpleName() + " start.Don't stop this program.");
								
			Set<String> repoNamesInProp = resourceBundle.keySet();//記錄github_repo.properties裡面已經有成員的repository name清單
			Set<String> repoNameWithoutUserId = new HashSet<String>();//記錄github_repo.properties裡面只有key(repository name)值，而value(userId)值為空("")的所有key(repository name)值清單
			for (String repoNameInProp : repoNamesInProp) {
				if(resourceBundle.getString(repoNameInProp).equals("")){
					repoNameWithoutUserId.add(repoNameInProp);
				}
			}
			for (String repoNameInProp : repoNameWithoutUserId) {
				//移除不包含任何userId的repository name
				repoNamesInProp.remove(repoNameInProp);
			}
			logger.debug("repoNamesInProp :" + repoNamesInProp);			
			
			ValidateResult validateRepoNamesResult = validateRepoNames(repoNamesInProp);
			ValidateResult validateUserIdsResult = validateUserIds(repoNamesInProp);
			
			//驗証失敗時就直接跳離程式不再繼續執行
			if(!validateRepoNamesResult.isSuccess() || !validateUserIdsResult.isSuccess()){
				StringBuilder validateFailReason = new StringBuilder();
				if(!validateRepoNamesResult.isSuccess()){
					validateFailReason.append(validateRepoNamesResult.getValidateFailReason());
				}
				
				if(!validateUserIdsResult.isSuccess()){
					validateFailReason.append(validateUserIdsResult.getValidateFailReason());
				}
				
				logger.error(validateFailReason);
				return;
			}
			
			for (String repoNameInProp : repoNamesInProp) {
				List<String> userIdsInGitHub = gitHubUtil.getAllCollaboratorLoginId(repoNameInProp);//GitHub裡面特定repository name所對應的userId清單
				List<String> userIdsInProp = Arrays.asList(resourceBundle.getString(repoNameInProp).replaceAll(" ", "").split(","));//github_repo.properties裡面特定repository name所對應的userId清單	
				Set<String> userIdsInGitHubOrInProp = new HashSet<String>();//存在於userIdsInGitHub或userIdsInProp裡的userId清單(這二個變數取聯集)
				userIdsInGitHubOrInProp.addAll(userIdsInGitHub);
				userIdsInGitHubOrInProp.addAll(userIdsInProp);							
				
				List<String> needToAddUser = new ArrayList<String>();
				List<String> needToRemoveUser = new ArrayList<String>();
				for (String userId : userIdsInGitHubOrInProp) {
					//更新Collaborator之前已存在但是更新後不存在的user，代表是要被刪除的user
					if(userIdsInGitHub.contains(userId) && !userIdsInProp.contains(userId)){
						needToRemoveUser.add(userId);
					}
					
					//更新Collaborator之前不存在但是更新後存在的user，代表是要被增加的user
					if(!userIdsInGitHub.contains(userId) && userIdsInProp.contains(userId)){
						needToAddUser.add(userId);
					}
				}
				logger.debug("repoName : " + repoNameInProp + " / userIdsInGitHub : " + userIdsInGitHub + 
							" / userIdsInProp : " + userIdsInProp +
							" / needToAddUser : " + needToAddUser + " / needToRemoveUser : " + needToRemoveUser);
				
				for (String userId : needToAddUser) {
					gitHubUtil.addCollaborator(repoNameInProp, userId);
				}
				
				for (String userId : needToRemoveUser) {
					gitHubUtil.removeCollaborator(repoNameInProp, userId);
				}
			}			
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		finally{
			logger.info(RepoUserImporter.class.getSimpleName() + " stop.");	
		}		
	}

	/**
	 * 驗証repository name
	 * 
	 * @param repoNamesInProp	github_repo.properties裡面已經有成員的repository name清單
	 * 
	 * @return	ValidateResult
	 */
	private static ValidateResult validateRepoNames(Set<String> repoNamesInProp){
		List<String> repoNamesInGitHub = gitHubUtil.getAllRepositoryName();
		StringBuilder validateFailReason = new StringBuilder();
		for (String repoNameInProp : repoNamesInProp) {
			if(!repoNamesInGitHub.contains(repoNameInProp)){
				validateFailReason.append("[" + repoNameInProp + "] ");
			}
		}
		if(validateFailReason.length() > 0){
			validateFailReason.append("repositories not in GitHub.");
			return new ValidateResult(false, validateFailReason.toString());
		}
		else{
			return new ValidateResult(true, null);
		}
		
	}
	
	/**
	 * 驗証userId
	 * 
	 * @param repoNamesInProp	github_repo.properties裡面已經有成員的repository name清單
	 * 
	 * @return	ValidateResult
	 */
	private static ValidateResult validateUserIds(Set<String> repoNamesInProp){		
		Set<String> allUserIdsInProp = new HashSet<String>();//github_repo.properties裡面已經有成員的repository name清單中，所有userId去掉重覆名稱後的清單
		for (String repoNameInProp : repoNamesInProp) {
			String[] userIdsArray = resourceBundle.getString(repoNameInProp).replaceAll(" ", "").split(",");//預防有人typo多打了" "，要先把" "去掉後再切割字串
			allUserIdsInProp.addAll(Arrays.asList(userIdsArray));
		}
		logger.debug("allUserIdsInProp :" + allUserIdsInProp);
		
		StringBuilder validateFailReason = new StringBuilder();
		//檢查userId是否為空字串("")
		if(allUserIdsInProp.contains("")){
			validateFailReason.append("Find some userId is empty in property file(e.g. xxxRep=userA,,userC).");
		}
		
		//檢查GitHub裡是否存在property檔裡指定的userId
		for (String userIdInProp : allUserIdsInProp) {			
			if(!userIdInProp.equals("") && !gitHubUtil.isGitHubUser(userIdInProp)){
				validateFailReason.append("[" + userIdInProp + "] user not in GitHub.");
			}
		}	
		
		//檢查property檔裡每個repository設定的userId值是否有重覆
		for (String repoNameInProp : repoNamesInProp) {
			List<String> userIdsAsList = Arrays.asList(resourceBundle.getString(repoNameInProp).replaceAll(" ", "").split(","));//預防有人typo多打了" "，要先把" "去掉後再切割字串
			Set<String> userIdsAsSet = new HashSet<String>();
			Collections.addAll(userIdsAsSet, resourceBundle.getString(repoNameInProp).replaceAll(" ", "").split(","));//預防有人typo多打了" "，要先把" "去掉後再切割字串
			
			//轉成List跟Set後發現二者的筆數不同，代表有userId值重覆了
			if(userIdsAsList.size() != userIdsAsSet.size()){
				validateFailReason.append("Find duplicate userId values in property file(error at [" + repoNameInProp + "] key).");
			}
		}
		
		if(validateFailReason.length() > 0){			
			return new ValidateResult(false, validateFailReason.toString());
		}
		else{
			return new ValidateResult(true, null);
		}
		
	}	
	
	private static class ValidateResult{
		private boolean isSuccess;
		private String validateFailReason;
		
		public ValidateResult(boolean isSuccess,String validateFailReason) {
			this.isSuccess = isSuccess;
			this.validateFailReason = validateFailReason;
		}

		public boolean isSuccess() {
			return isSuccess;
		}

		public String getValidateFailReason() {
			return validateFailReason;
		} 				
	}
}
