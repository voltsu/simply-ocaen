package cn.simplyocean.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.simplyocean.activity.FeedBackActivity;
import cn.simplyocean.activity.R;
import cn.simplyocean.asyncimageloader.FileUtils;
import cn.simplyocean.utils.CommonUtils;

/**
 * ��ҳ ���� fragment
 *
 */
public class SettingFragment extends BaseFragment implements OnClickListener {

	private static final String TAG = "SettingFragment";
	private Activity mActivity;
	private TextView mTitleTv;
	// �Ƽ���΢�ź���
	//private RelativeLayout mRecommondToWeixinLayout;
	// �������
	private RelativeLayout mFeedbackLayout;
	// ��������
	private RelativeLayout mAboutUsLayout;
	// Ӧ���Ƽ�
	//private RelativeLayout mAppRecommendLayout;
	// �������
	private RelativeLayout mClearCacheLayout;

	public static SettingFragment newInstance() {
		SettingFragment settingFragment = new SettingFragment();

		return settingFragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_setting, container,
				false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initViews(view);
		initEvents();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void initViews(View view) {
		mTitleTv = (TextView) view.findViewById(R.id.title_tv);
		mTitleTv.setText(R.string.setting);
		
		//mRecommondToWeixinLayout = (RelativeLayout) view.findViewById(R.id.recommond_to_weixin_layout);
		mFeedbackLayout = (RelativeLayout) view.findViewById(R.id.feedback_layout);
		mAboutUsLayout = (RelativeLayout) view.findViewById(R.id.about_us_layout);
		//mAppRecommendLayout = (RelativeLayout) view.findViewById(R.id.app_recommend_layout);
		mClearCacheLayout = (RelativeLayout) view.findViewById(R.id.clear_cache_layout);
	}
	
	private void initEvents() {
		//mRecommondToWeixinLayout.setOnClickListener(this);
		mFeedbackLayout.setOnClickListener(this);
		mAboutUsLayout.setOnClickListener(this);
		//mAppRecommendLayout.setOnClickListener(this);
		mClearCacheLayout.setOnClickListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public String getFragmentName() {
		return TAG;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//case R.id.recommond_to_weixin_layout:
			// �Ƽ���΢�ź���
		//	break;
		case R.id.feedback_layout:
			// �������
			CommonUtils.launchActivity(mActivity, FeedBackActivity.class);
			break;
		case R.id.about_us_layout:
			// ��������
			break;
		//case R.id.app_recommend_layout:
			// Ӧ���Ƽ�
		//	break;
		case R.id.clear_cache_layout:
			// �������
			FileUtils file = new FileUtils(getActivity());
			file.deleteFile();
			Toast toast = Toast.makeText(getActivity(), "���������ɹ���", Toast.LENGTH_SHORT);
			toast.show();
			break;

		default:
			break;
		}
	}

}