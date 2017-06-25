

项目涵盖Launcher和服务器交互的所有内容。


使用方法见  ClientDemo


Resouces:

----------------------------------------------------------------------------


桌面后台配置地址 release

http://tlauncher-cms.tclclouds.com/tlauncher-boss/admin/login


项目和服务器的接口说明

http://confluence.lab.tclclouds.com/pages/viewpage.action?pageId=13484865



-----------------------------------------------------------------------------



后台配置项命名规则：

	项目－渠道－KeyName


项目：
	hilauncher
	joylauV1
	joylauv3


渠道

	google
	appcenter


Key 列表

	note:	
		命名规则: 
			1:尽量用【动词_名词的结构】如:show_hotgame

								
search_order
show_hot_game
show_hot_site
show_hot_word
show_lock_screen
news_stream_ads_switch
news_large_image_ads_switch
screen_saver_ads_switch
widget_ads_switch
screen_saver_switch
get_lockscreen_config_interval
single_notification_ads_switch
news_switch
default_search_engine
drawer_recommend
folder_recommend
ad_count
forbed_time
boost_style
ads_boost_id
feedback_google
feedback_facebook


升级规则：　
	客户断1.3.0以后全部用新的Key
	服务器原有的Key-value保持，为了支持老的版本。


关于Key设计的原则是
	好处：Key名字的唯一性，后台不打算改变接口。
	坏处：本地配置项目加一个就要加好几个。






