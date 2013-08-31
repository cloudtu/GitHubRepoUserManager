package cloudtu.github;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.apache.log4j.Logger;
import cloudtu.util.GitHubUtil;
import cloudtu.util.SysConfig;
import javax.swing.JLabel;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JList;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class RepoUserGuiManager extends JFrame
{
    private static final Logger logger = Logger.getLogger(RepoUserGuiManager.class);  
    private static final String GUI_LOOK_AND_FEEL = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";    

    private JComboBox repoComboBox;
    private JList includeUserList;
    private JList excludeUserList;
    private DefaultListModel includeUserListModel = new DefaultListModel();
    private DefaultListModel excludeUserListModel = new DefaultListModel();
    private JLabel processResultLabel;
    private GitHubUtil gitHubUtil = new GitHubUtil(SysConfig.getString("AdminLoginId"), SysConfig.getString("AdminLoginPwd"));
    private List<String> allUserId = Arrays.asList(SysConfig.getString("AllCollaboratorLoginId").split(","));
    
    /**
     * Create the frame.
     */
    public RepoUserGuiManager()
    {
    	Collections.sort(allUserId);
    	
        setTitle(RepoUserGuiManager.class.getSimpleName());
    	setResizable(false);        
        setBounds(100, 100, 395, 475);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);          
        getContentPane().setLayout(null);
        
        JLabel repoTitleLabel = new JLabel("repository : ");
        repoTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        repoTitleLabel.setBounds(6, 6, 68, 26);
        getContentPane().add(repoTitleLabel);
        
        repoComboBox = new JComboBox();
        repoComboBox.setBounds(73, 6, 262, 26);
        getContentPane().add(repoComboBox);
        List<String> repoNames = gitHubUtil.getAllRepositoryName();
        for (String repoName : repoNames) {
        	repoComboBox.addItem(repoName);
		}        
        repoComboBox.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		updateUIWhenRepoComboBoxChange();
        	}
        });        
        
        JLabel includeUserLabel = new JLabel("include user");
        includeUserLabel.setHorizontalAlignment(SwingConstants.CENTER);
        includeUserLabel.setBounds(23, 44, 116, 18);
        getContentPane().add(includeUserLabel);
        
        JScrollPane incloudeUserScrollPane = new JScrollPane();
        incloudeUserScrollPane.setBounds(6, 64, 150, 310);
        getContentPane().add(incloudeUserScrollPane);
                        
        includeUserList = new JList(includeUserListModel);
        incloudeUserScrollPane.setViewportView(includeUserList);
        
        JButton includeButton = new JButton("<");
        includeButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		Object[] userIds = excludeUserList.getSelectedValues();
        		for (Object userId : userIds) {
					excludeUserListModel.removeElement(userId);
					includeUserListModel.addElement(userId);
				}
        	}
        });
        includeButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        includeButton.setBounds(168, 165, 40, 40);
        getContentPane().add(includeButton);
        
        JButton excludeButton = new JButton(">");
        excludeButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		Object[] userIds = includeUserList.getSelectedValues();
        		for (Object userId : userIds) {
					includeUserListModel.removeElement(userId);
					excludeUserListModel.addElement(userId);
				}        		
        	}
        });
        excludeButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        excludeButton.setBounds(168, 217, 40, 40);
        getContentPane().add(excludeButton);
        
        JLabel excludeUserLabel = new JLabel("exclude user");
        excludeUserLabel.setHorizontalAlignment(SwingConstants.CENTER);
        excludeUserLabel.setBounds(237, 44, 116, 18);
        getContentPane().add(excludeUserLabel);
        
        JScrollPane excloudeUserScrollPane = new JScrollPane();
        excloudeUserScrollPane.setBounds(220, 64, 150, 310);
        getContentPane().add(excloudeUserScrollPane);        
        
        excludeUserList = new JList(excludeUserListModel);
        excloudeUserScrollPane.setViewportView(excludeUserList);
        
        JButton updateButton = new JButton("update");
        updateButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		try {
        			processResultLabel.setText("");
        			String repoName = repoComboBox.getSelectedItem().toString();
					List<String> userListBeforeSubmit = gitHubUtil.getAllCollaboratorLoginId(repoName);
					List<String> needToAddUser = new ArrayList<String>();
					List<String> needToRemoveUser = new ArrayList<String>();
					for (String userId : allUserId) {
						//更新Collaborator之前已存在但是更新後不存在的user，代表是要被刪除的user
						if(userListBeforeSubmit.contains(userId) && !includeUserListModel.contains(userId)){
							needToRemoveUser.add(userId);
						}
						
						//更新Collaborator之前不存在但是更新後存在的user，代表是要被增加的user
						if(!userListBeforeSubmit.contains(userId) && includeUserListModel.contains(userId)){
							needToAddUser.add(userId);
						}
					}
					
					logger.debug("needToAddUser : " + needToAddUser);
					logger.debug("needToRemoveUser : " + needToRemoveUser);
					
					for (String user : needToAddUser) {
						gitHubUtil.addCollaborator(repoName, user);
					}
					for (String user : needToRemoveUser) {
						gitHubUtil.removeCollaborator(repoName, user);
					}					
					processResultLabel.setText("update success");
				}
        		catch (Exception ex) {
        			logger.error(ex.getMessage(), ex);
					processResultLabel.setText("error occur : " + ex.getMessage());
				}
        	}
        });
        updateButton.setBounds(6, 386, 90, 30);
        getContentPane().add(updateButton);
        
        processResultLabel = new JLabel("");
        processResultLabel.setBounds(108, 386, 262, 30);
        getContentPane().add(processResultLabel);
        
        updateUIWhenRepoComboBoxChange();
    }
    
    private void updateUIWhenRepoComboBoxChange(){
		try {
			processResultLabel.setText("");
			String repoName = repoComboBox.getSelectedItem().toString();
			List<String> includeUserIdList = gitHubUtil.getAllCollaboratorLoginId(repoName);
			List<String> excludeUserIdList = new ArrayList<String>(allUserId);
			includeUserListModel.clear();
			for (String userId : includeUserIdList) {
				includeUserListModel.addElement(userId);
				excludeUserIdList.remove(userId);
			}
			excludeUserListModel.clear();
			for (String userId : excludeUserIdList) {
				excludeUserListModel.addElement(userId);
			}
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			processResultLabel.setText("error occur : " + ex.getMessage());
		}    	
    }

    
    /**
     * 為系統中所有未被catch的Thread註冊UncaughtExceptionHandler
     */
    private static void registerUncaughtExceptionHandler(){
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){            
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error(e.getMessage(), e);
            }           
        });
    }   
    
    /**
     * Launch Application
     * 
     * @throws Exception 
     */
    private static void lunchApp() throws Exception{
        UIManager.setLookAndFeel(GUI_LOOK_AND_FEEL);        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                RepoUserGuiManager app = new RepoUserGuiManager();
                app.setVisible(true);
            }
        });
    }
    
    public static void main(String args[]) throws Exception {
        registerUncaughtExceptionHandler();
        lunchApp();
    }       
}
