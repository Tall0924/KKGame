package com.xtone.game87873.contants;

/**
 * ApiUrl.java
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-7 下午3:17:40
 */
public class ApiUrl {

	public static final String BASE_URL = "http://api.87873.cn";
//	public static final String BASE_URL = "http://api.mykaku.com";
//	public static final String BASE_URL = "http://125.77.254.58:9500";
//	public static final String BASE_URL = "http://192.168.50.16";

	public static final String CHECK_UPDATE = "/version/infor";
	public static final String HOME_BANNER = "/index/homebanner"; //横幅
	public static final String HOME_AD = "/index/homeadcard";
	public static final String HOME_GAME_LIST = "/index/homegamelist";
	public static final String HOME_TAG_LIST = "/index/hometaglist";
	// 1为web端，2为app端
	public static final int SOURCE_APP = 2;
	public static final int PAGE_SIZE = 12;// 每页加载的数量
	// 发验证码的类型
	public static final int SEND_CODE_TYPE_REGISTER = 1;// 用于注册
	public static final int SEND_CODE_TYPE_FIND_PASSWORD = 2;// 用于找回密码
	// 加载页广告
	public static final String INDEX_AD = "/app/indexad";
	// 分类
	public static final String TYPE_LIST = "/page/typelist";
	public static final String TAG_LIST_BY_TYPE = "/page/taglistbytype";
	public static final String GAME_LIST_BY_TYPE = "/page/gamelistbytype";
	public static final String GAME_LIST_BY_TAG = "/page/gamelistbytag";
	// 排行
	public static final String RANK_LIST = "/page/ranklist";
	// 游戏详情
	public static final String GAME_INFO = "/page/gameinfo";
	public static final String GIFT_LIST_BY_GAME = "/libao/libaolistbygame";
	// 游戏点击/下载统计
	public static final String GAME_STAT = "/page/gamestatisticsincr";
	// 收藏
	public static final String ADD_COLLECTION = "/user/AddCollection";
	public static final String COLLECTION_LIST = "/user/CollectionList";
	public static final String DEL_COLLECTION = "/user/DelCollection";
	// 我的游戏
	public static final String UPDATE_LIST = "/version/gameversion";
	public static final String INSTALL_LIST = "/version/installed";
	// 专题列表
	public static final String SUBJECT_LIST = "/topic/topiclist";
	public static final String SUBJECT_INFO = "/topic/topicinfo";
	public static final String GAME_LIST_BY_SUBJECT = "/topic/gamelistbytopic";
	// 资讯
	public static final String INFORMATION_LIST = "/Information/InforList";
	public static final String INFORMATION_INFO = "/Information/InforInfo";
	// 福利首页banner
	public static final String LIBAO_BANNER = "/advert/LibaoAd";
	// 获取礼包列表
	public static final String LIBAO_LIST = "/libao/libaolist";
	// 搜索礼包
	public static final String LIBAO_SEARCH = "/libao/libaoSearch";
	// 获取我的礼包
	public static final String GET_MY_LIBAO = "/user/getmylibao";
	// 礼包状态 1、抢号、2淘号、3结束、4预约
	public static final int GIFT_STATUS_TAKE_NO = 1;
	public static final int GIFT_STATUS_FOR_NO = 2;
	public static final int GIFT_STATUS_FINISH = 3;
	public static final int GIFT_STATUS_ORDER = 4;
	// 礼包详情
	public static final String LIBAO_INFO = "/libao/Libaoinfo";
	// 领取礼包
	public static final String LIBAO_RECEIVE = "/libao/LibaoReceive";
	// 礼包淘号
	public static final String LIBAO_TAO_HAO = "/libao/LibaoTaohao";
	// 删除礼包
	public static final String DELETE_MY_LIBAO = "/user/delmylibao";
	// 搜索热词列表
	public static final String SEARCH_HOT_WORD = "/search/searchHotWord";
	// 换一批搜索热词列表
	public static final String ROUND_HOT_WORD = "/search/roundHotWord";
	// 游戏搜索
	public static final String SEARCH_GAME = "/search/search";
	// 提交意见反馈
	public static final String ADD_FEEDBACK = "/feedback/add";
	// 获取意见反馈
	public static final String GET_FEEDBACK = "/Feedback/userFeedback";
	// 发送验证码
	public static final String SEND_SMS_CODE = "/user/sendcode";
	// 手机验证
	public static final String VERIFY_MOBILE = "/user/VerifyMobile";
	// 找回密码
	public static final String USER_REPWD = "/user/repwd";
	// 注册
	public static final String USER_REGISTER = "/user/register";
	public static final String USER_REGISTER_BY_ACCOUNT = "/user/regbyaccount";
	// 登录
	public static final String USER_LOGIN = "/user/login";
	// 获取用户指定字段值
	public static final String USER_GET_FIELD = "/user/getField";
	// 修改用户资料
	public static final String EDIT_USER = "/user/edituser";
	// 上传头像
	public static final String UPLOAD_FIGURE = "/user/uploadfigure";
}
