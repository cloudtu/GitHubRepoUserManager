package cloudtu.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;

/**
 * GitHub Utility<p/>
 *
 * @version
 * <ol>
 * 	<li>CloudTu：First Release</li> 
 * </ol>
 */
public class GitHubUtil {		
	private static final Logger logger = Logger.getLogger(GitHubUtil.class);  
	
	private GitHubClient client = new GitHubClient();
	private CollaboratorService collaboratorService = new CollaboratorService(client);
	private RepositoryService repositoryService = new RepositoryService(client);
	private UserService userService = new UserService(client); 
	
	/**
	 * GitHub Utility
	 * 
	 * @param adminLoginId	repository admin login id
	 * @param adminLoginPwd	repository admin login password
	 */
	public GitHubUtil(String adminLoginId,String adminLoginPwd){
		client.setCredentials(adminLoginId, adminLoginPwd);		
	}	
	
	/**
	 * 檢查GitHub是否有該名使用者帳號
	 * 
	 * @param userLoginId	user login id
	 * 
	 * @return	true:有該名使用者帳號,false:無該名使用者帳號
	 */
	public synchronized boolean isGitHubUser(String userLoginId){
		try {
			userService.getUser(userLoginId);
			return true;
		}
		catch(IOException e){
			logger.debug(e.getMessage() ,e);
			return false;
		}
	}
	
	/**
	 * 查詢admin所擁有的所有repository名稱
	 * 
	 * @return	admin所擁有的所有repository名稱
	 * 
	 * @throws	RuntimeException
	 */
	public synchronized List<String> getAllRepositoryName(){
		try {
			List<String> repoNameList = new ArrayList<String>();
			for (Repository repository : repositoryService.getRepositories()) {
				repoNameList.add(repository.getName());
			}
			Collections.sort(repoNameList);
			return repoNameList;
		}
		catch(Exception e) {
			throw new RuntimeException("get all repository name fail" ,e);
		}	
	}		
	
	/**
	 * 查詢特定repository裡所有共同開發者的登入id
	 * 
	 * @param repoName	repository name
	 * 
	 * @return	特定repository裡所有共同開發者的登入id
	 * 
	 * @throws	RuntimeException
	 */
	public synchronized List<String> getAllCollaboratorLoginId(String repoName){
		try {
			RepositoryId repoId = new RepositoryId(client.getUser(), repoName);
			List<String> collaboratorLoginIdList = new ArrayList<String>();
			for (User collaborator : collaboratorService.getCollaborators(repoId)) {
				if(!collaborator.getLogin().equals(client.getUser())){
					collaboratorLoginIdList.add(collaborator.getLogin());					
				}
			}
			Collections.sort(collaboratorLoginIdList);
			return collaboratorLoginIdList;
		}
		catch(Exception e) {
			throw new RuntimeException("get all collaborator login id in [" + repoName + "] repository fail" ,e);
		}	
	}	
	
	/**
	 * 在特定repository裡增加新的共同開發者
	 * 
	 * @param repoName		repository name
	 * @param userLoginId	collaborator login id
	 * 
	 * @return	true:success
	 * 
	 * @throws	RuntimeException 
	 */
	public synchronized void addCollaborator(String repoName,String userLoginId){
		try {
			RepositoryId repoId = new RepositoryId(client.getUser(), repoName);
			collaboratorService.addCollaborator(repoId, userLoginId);
		}
		catch (Exception e) {
			throw new RuntimeException("add [" + userLoginId + "] collaborator to [" + repoName + "] repository fail",e);
		}
	}
	
	/**
	 * 在特定repository裡移除現有的共同開發者
	 * 
	 * @param repoName		repository name
	 * @param userLoginId	collaborator login id
	 * 
	 * @throws	RuntimeException
	 */
	public synchronized void removeCollaborator(String repoName,String userLoginId){
		try {
			RepositoryId repoId = new RepositoryId(client.getUser(), repoName);
			collaboratorService.removeCollaborator(repoId, userLoginId);
		}
		catch (Exception e) {
			throw new RuntimeException("remove [" + userLoginId + "] collaborator from [" + repoName + "] repository fail",e);
		}
	}		
}
