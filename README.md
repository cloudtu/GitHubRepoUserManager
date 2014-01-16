GitHubRepoUserManager
=====================

管理GitHub每個repository成員的工具

介紹
----

在GitHub要管理每個repository成員要上GitHub管理網頁一個個成員慢慢加上去，如果你建了多個repository，管理成員這件事會變的痛苦又花時間，
因此寫了個這個懶人工具來幫忙完成這件任務。

專案目錄簡述
-----------
*   src : 放所有java class與設定檔(*.properties)
*   lib : 放library
*   lib.src : 放 library source jar
*   img : wiki或readme會用到的圖片
*   script : 放啟動程式的script

可以直接使用的懶人包
---------------------
*   [v1.0](https://github.com/cloudtu/GitHubRepoUserManager/releases/download/v1.0/GitHubRepoUserManager_v1.0.zip)

使用說明簡述
-----------
1.   設定 sysconfig.properties
  *   `AdminLoginId` 設定 repository管理員id
  *   `AdminLoginPwd` 設定 repository管理員password
  *   `AllCollaboratorLoginId` 設定管理員之外的所有成員id，每個成員用 `,` 做間隔
2.   修改 log4j.properties 裡的設定值可以調整log檔記錄明細  
3.   GUI介面(java swing)的repository成員管理工具 : 可決定要在哪個repository裡面加進成員或移出成員
  *   修改 RepoUserGuiManager.bat(or RepoUserGuiManager.sh) 裡的環境變數
  *   執行 RepoUserGuiManager.bat(or RepoUserGuiManager.sh)
  *   執行畫面如下所示

      ![RepoUserGuiManager.png](https://raw.github.com/cloudtu/GitHubRepoUserManager/master/img/RepoUserGuiManager.png)
4.   整批匯出工具 : 可以把所有 repository與對應的成員匯成 `repo_user.properties` 檔案
  *   修改 RepoUserExporter.bat(or RepoUserExporter.sh) 裡的環境變數
  *   執行 RepoUserExporter.bat(or RepoUserExporter.sh)
5.   整批匯入工具 : 可以依據 `repo_user.properties` 檔案的設定，把所有設定匯入GitHub  
  *   修改 RepoUserImporter.bat(or RepoUserImporter.sh) 裡的環境變數
  *   執行 RepoUserImporter.bat(or RepoUserImporter.sh)
