package cloudtu.github;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import cloudtu.util.GitHubUtil;
import cloudtu.util.SortedProperties;
import cloudtu.util.SysConfig;

public class RepoUserExporter {
	private static final Logger logger = Logger.getLogger(RepoUserExporter.class);  
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		logger.info(RepoUserExporter.class.getSimpleName() + " start.Don't stop this program.");
		try {
			GitHubUtil gitHubUtil = new GitHubUtil(SysConfig.getString("AdminLoginId"), SysConfig.getString("AdminLoginPwd"));
			Properties prop = new SortedProperties();
			List<String> repoNames = gitHubUtil.getAllRepositoryName();
			for (String repoName : repoNames) {
				List<String> userIds = gitHubUtil.getAllCollaboratorLoginId(repoName);
				StringBuilder userIdsStr = new StringBuilder("");
				for (String userId : userIds) {
					userIdsStr.append(userId + ",");
				}
				if(userIds.size() > 0){
					userIdsStr.deleteCharAt(userIdsStr.length() - 1);
				}
				prop.setProperty(repoName, userIdsStr.toString());
			}
			prop.store(new FileOutputStream(RepoUserExporter.class.getResource("/").getPath() + "repo_user.properties"), null);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info(RepoUserExporter.class.getSimpleName() + " stop.");
	}

}
