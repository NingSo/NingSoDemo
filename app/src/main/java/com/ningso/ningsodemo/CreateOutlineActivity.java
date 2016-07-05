package com.ningso.ningsodemo;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ningso.ningsodemo.utils.KeyboardHelper;
import com.ningso.ningsodemo.widgets.flattop.AirText.TextInfo;
import com.ningso.ningsodemo.widgets.flattop.Flattop;

/**
 * Created by NingSo on 16/4/15.下午11:55
 *
 * @author: NingSo
 * @Email: ningso.ping@gmail.com
 */
public class CreateOutlineActivity extends AppCompatActivity implements View.OnClickListener, KeyboardHelper.OnKeyboardVisibilityListener, Flattop.AirTextRequestListener {
    @Override
    public void onRequestFontFocus(TextInfo textInfo) {

    }

    @Override
    public void onRequestInput(Object obj) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onVisibilityChanged(boolean z) {

    }
//
//    private OutlineMaterialAdapter mAdapter;
//    private View mBtnBack;
//    private View mBtnRight;
//    private View mBtnSend;
//    private AirText.TextInfo mCurrentTextInfo;
//    private List<String> mDatas;
//    private Flattop mFlattop;
//    private FontAdapter mFontAdapter;
//    private List<Font> mFonts;
//    private GridView mGridView;
//    private GridView mGridViewFont;
//    private EditText mInputEdit;
//    private View mInputLayout;
//    private boolean mIsBlank;
//    private boolean mIsCheckingBackground;
//    private KeyboardHelper mKeyboardHelper;
//    private View mLayoutMaterial;
//    private String mMaterialCategory;
//    private String mOutlineName;
//    private MomiRadioGroup mRadioGroup;
//    private List<String> mRealBackground;
//
//
//    class C07222 implements AdapterView.OnItemClickListener {
//        C07222() {
//        }
//
//        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//            CreateOutlineActivity.this.checkMaterial(position);
//        }
//    }
//
//    class C07233 implements AdapterView.OnItemClickListener {
//        C07233() {
//        }
//
//        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//            String targetString;
//            final Font font =CreateOutlineActivity.this.mFonts.get(position);
//            TextInfo ti = CreateOutlineActivity.this.mFlattop.getFlyingTextInfo();
//            if (ti == null) {
//                targetString = CreateOutlineActivity.this.getResources().getString(R.string.font_please_input_words);
//            } else {
//                targetString = ti.text;
//            }
//            final String str = targetString;
//            WaitingSpinner.show(CreateOutlineActivity.this);
//            FontCenter.getInstance().getCloudTypeface(font, str, new FontTypefaceCallBack() {
//                public void onSuccess(String s, Typeface typeface) {
//                    WaitingSpinner.dismiss();
//                    TextInfo textInfo = new TextInfo();
//                    textInfo.text = str;
//                    textInfo.font = font;
//                    textInfo.typeface = typeface;
//                    CreateOutlineActivity.this.mFlattop.addElement(textInfo);
//                }
//
//                public void onFailure(FailureInfo failureInfo) {
//                    WaitingSpinner.dismiss();
//                    ToastUtils.show(failureInfo.getErrorMessage());
//                }
//            });
//        }
//    }
//
//
//    class C23575 implements OnLoadListener {
//        C23575() {
//        }
//
//        public void onLoad(int error, String errorMsg, Bitmap bm) {
//            CreateOutlineActivity.this.mFlattop.addElement(bm);
//        }
//    }
//
//    class C23608 implements OnProxySigninListener {
//        C23608() {
//        }
//
//        public void onSignin(int error) {
//            if (error == SigninProxy.SIGNUP) {
//                ToastUtils.show((int) C0752R.string.need_signin);
//                CreateOutlineActivity.this.startActivity(new Intent(CreateOutlineActivity.this, SigninActivity.class));
//            }
//        }
//    }
//
//    class C23619 implements OnAlertListener {
//        C23619() {
//        }
//
//        public void onAlertClick(Fragment frag, AlertButton which) {
//            if (which == AlertButton.BTN_OK) {
//                CreateOutlineActivity.this.mOutlineName = ((EditDialogFragment) frag).getText().toString();
//                CreateOutlineActivity.this.preUpload();
//            }
//        }
//    }
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_create_outline);
//        initViews();
//        getWindow().setFlags(ViewCompat.MEASURED_STATE_TOO_SMALL, ViewCompat.MEASURED_STATE_TOO_SMALL);
//        init();
//    }
//
//    private void init() {
//        getAllFontListByLanguage();
//        this.mRadioGroup.setOnCheckedChangeListener(new MomiRadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(MomiRadioGroup momiRadioGroup, int i) {
//                if (checkedId == R.id.outline_font) {
//                    CreateOutlineActivity.this.showGridView(C0752R.id.outline_font);
//                } else {
//                    CreateOutlineActivity.this.bindData(checkedId);
//                }
//            }
//        });
//        this.mGridView.setOnItemClickListener(new C07222());
//        this.mGridViewFont.setOnItemClickListener(new C07233());
//        loadRealBackground();
//        bindData(R.id.outline_background);
//        checkMaterial(0);
//    }
//
//    private void checkMaterial(final int position) {
//        ImageHelper imageHelper = new ImageHelper();
//        if (this.mIsCheckingBackground) {
//            imageHelper.load(this, (String) this.mRealBackground.get(position), true, new OnLoadListener() {
//                public void onLoad(int error, String errorMsg, Bitmap bm) {
//                    CreateOutlineActivity.this.mIsBlank = position == 10;
//                    CreateOutlineActivity.this.mFlattop.setBackgroundBitmap(bm);
//                }
//            });
//        } else {
//            imageHelper.load(this, (String) this.mDatas.get(position), true, new C23575());
//        }
//    }
//
//    private void getAllFontListByLanguage() {
//        FontCenter.getInstance().getFontListByTag(new String[]{"font"}, 1, 20, new IHttpCallBack<ArrayList<Font>>() {
//            @Override
//            public void onSuccess(ArrayList<Font> fonts) {
//                CreateOutlineActivity.this.mFonts = fonts;
//                CreateOutlineActivity.this.bindFontData();
//            }
//
//            @Override
//            public void onErr(String s) {
//                ToastUtils.show(s);
//            }
//        });
//    }
//
//    private void bindFontData() {
//        if (this.mFontAdapter == null) {
//            this.mFontAdapter = new FontAdapter(this.mFonts);
//            this.mGridViewFont.setAdapter(this.mFontAdapter);
//        } else {
//            this.mFontAdapter.notifyDataSetChanged();
//        }
//        setupGridView(this.mGridViewFont, this.mFonts);
//    }
//
//    private void loadRealBackground() {
//        this.mRealBackground = new ArrayList();
//        loadAssetsData(this.mRealBackground, Constants.OUTLINE_MATERIAL_CATEGORY_BACKGROUND);
//    }
//
//    private void bindData(int checkedId) {
//        switch (checkedId) {
//            case R.id.outline_background:
//                this.mIsCheckingBackground = true;
//                this.mMaterialCategory = Constants.OUTLINE_MATERIAL_CATEGORY_BACKGROUND_THUMBNAIL;
//                break;
//            case R.id.outline_flower:
//                this.mIsCheckingBackground = false;
//                this.mMaterialCategory = Constants.OUTLINE_MATERIAL_CATEGORY_FLOWER;
//                break;
//            case R.id.outline_birds:
//                this.mIsCheckingBackground = false;
//                this.mMaterialCategory = Constants.OUTLINE_MATERIAL_CATEGORY_BIRDS;
//                break;
//        }
//        this.mDatas = new ArrayList();
//        loadAssetsData(this.mDatas, this.mMaterialCategory);
//        if (this.mAdapter == null) {
//            this.mAdapter = new OutlineMaterialAdapter(this.mDatas);
//            this.mGridView.setAdapter(this.mAdapter);
//        } else {
//            this.mAdapter.setData(this.mDatas);
//            this.mAdapter.notifyDataSetChanged();
//        }
//        setupGridView(this.mGridView, this.mDatas);
//        showGridView(checkedId);
//    }
//
//    private void showGridView(int checkedId) {
//        if (checkedId == R.id.outline_font) {
//            findViewById(R.id.layout_grid_common).setVisibility(8);
//            findViewById(R.id.layout_grid_font).setVisibility(0);
//            return;
//        }
//        findViewById(R.id.layout_grid_common).setVisibility(0);
//        findViewById(R.id.layout_grid_font).setVisibility(8);
//    }
//
//    private void loadAssetsData(List<String> list, String cat) {
//        String baseStr = Constants.OUTLINE_ASSETS_BASE_PATH + cat;
//        try {
//            for (String str : getAssets().list(baseStr)) {
//                StringBuilder sb = new StringBuilder(baseStr);
//                sb.append("/").append(str);
//                list.add(sb.toString());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void setupGridView(GridView gridView, List data) {
//        int size = data.size();
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        float density = dm.density;
//        int itemWidth = (int) (100.0f * density);
//        gridView.setLayoutParams(new LayoutParams((int) (((float) ((size * 105) + 100)) * density), -1));
//        gridView.setColumnWidth(itemWidth);
//        gridView.setHorizontalSpacing(10);
//        gridView.setStretchMode(0);
//        gridView.setNumColumns(size);
//    }
//
//    private void initViews() {
//        this.mFlattop = (Flattop) findViewById(R.id.flattop);
//        this.mFlattop.setInputRequestListener(this);
//        this.mLayoutMaterial = findViewById(R.id.layout_outline_material);
//        this.mGridView = (GridView) findViewById(R.id.gridview_outline);
//        this.mGridViewFont = (GridView) findViewById(R.id.gridview_font);
//        this.mRadioGroup = (MomiRadioGroup) findViewById(R.id.outline_material_group);
//        this.mBtnBack = findViewById(C0752R.id.btn_back);
//        this.mBtnRight = findViewById(C0752R.id.btn_right);
//        this.mBtnBack.setOnClickListener(this);
//        this.mBtnRight.setOnClickListener(this);
//        this.mInputLayout = findViewById(R.id.input_layout);
//        this.mInputLayout.setVisibility(View.GONE);
//        this.mKeyboardHelper = new KeyboardHelper();
//        this.mKeyboardHelper.setKeyboardListener(this, this);
//        this.mInputEdit = (EditText) findViewById(C0752R.id.comment_edit);
//        this.mInputEdit.setHint(C0752R.string.text_hint_comment);
//        this.mBtnSend = findViewById(C0752R.id.btn_send);
//        this.mBtnSend.setOnClickListener(this);
//    }
//
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case C0752R.id.btn_back:
//                back();
//                return;
//            case C0752R.id.btn_right:
//                nameOutline();
//                return;
//            case C0752R.id.btn_send:
//                if (EmojiFilter.containsEmoji(this.mInputEdit.getText().toString().trim())) {
//                    ToastUtils.show((int) C0752R.string.font_invalidate_text);
//                    return;
//                }
//                if (!TextUtils.isEmpty(this.mInputEdit.getText().toString().trim())) {
//                    String content = this.mInputEdit.getText().toString().trim();
//                    this.mCurrentTextInfo.text = content;
//                    WaitingSpinner.show(this);
//                    FontCenter.getInstance().getCloudTypeface(this.mCurrentTextInfo.font, content, new FontTypefaceCallBack() {
//                        @Override
//                        public void onSuccess(String s, Typeface typeface) {
//                            WaitingSpinner.dismiss();
//                            CreateOutlineActivity.this.mCurrentTextInfo.typeface = typeface;
//                            CreateOutlineActivity.this.mFlattop.dispatchTextInfo(CreateOutlineActivity.this.mCurrentTextInfo);
//                        }
//
//                        @Override
//                        public void onFailure(FailureInfo failureInfo) {
//                            WaitingSpinner.dismiss();
//                            ToastUtils.show(failureInfo.getErrorMessage());
//                        }
//                    });
//                    this.mInputEdit.setText(BuildConfig.FLAVOR);
//                }
//                KeyboardHelper.hideKeyboard(this);
//                return;
//            default:
//                return;
//        }
//    }
//
//    private void nameOutline() {
//        if (new SigninProxy().signin(this, new C23608())) {
//            String nickname = ParamsCache.getNickname();
//            if (nickname != null) {
//                nickname = nickname.substring(0, Math.min(20, nickname.length()));
//            }
//            EditDialogFragment dialog = EditDialogFragment.create().setTitle(getString(C0752R.string.give_name_for_works)).setText(nickname).setConfirmText(getString(C0752R.string.confirm)).setOnAlertListener(new C23619());
//            dialog.setCancelable(true);
//            dialog.show(getSupportFragmentManager(), "name_outline");
//        }
//    }
//
//    private void preUpload() {
//        if (TextUtils.isEmpty(this.mOutlineName)) {
//            ToastUtils.show((R.string.name_for_template);
//            return;
//        }
//        Bitmap bitmap = this.mFlattop.getCombinedBitmap(this.mIsBlank);
//        String path = Directories.createImagePathToUpload();
//        WaitingSpinner.show(this);
//        new ImageHelper().save(this, bitmap, path, 1, 80, false, new OnSaveListener() {
//            public void onSave(int error, String errorMsg, String path) {
//                WaitingSpinner.dismiss();
//                if (error == 0) {
//                    CreateOutlineActivity.this.upload(path);
//                }
//            }
//        });
//    }
//
//    private void upload(String path) {
//        WaitingSpinner.show(this, true, false);
//        new UploadOutlineRequest(this.mOutlineName, path, new UpProgressHandler() {
//            public void progress(String var1, double var2) {
//                WaitingSpinner.getProgressListener().onProgressChanged(var2);
//            }
//        }).execute(new OnResponseListener() {
//            public void onGetResponse(HttpResponse httpResponse) {
//                WaitingSpinner.dismiss();
//                if (httpResponse.error == 0) {
//                    UploadOutlineResponse r = (UploadOutlineResponse) httpResponse;
//                    if (r.status == 0) {
//                        CreateOutlineActivity.this.saveToTemplate(r.tlId, r.tlLabel, r.tlName);
//                        return;
//                    } else {
//                        ToastUtils.show(r.reason);
//                        return;
//                    }
//                }
//                ToastUtils.show(httpResponse.errorMsg + " code:" + httpResponse.error);
//            }
//        });
//    }
//
//    private void saveToTemplate(String id, String label, String name) {
//        Bitmap bitmap = this.mFlattop.getCombinedBitmap(this.mIsBlank);
//        String path = Directories.getPathForEmptyTemplate(label);
//        final TemplateInfo template = new TemplateInfo(id, label, name, path);
//        WaitingSpinner.show(this);
//        new ImageHelper().save(this, bitmap, path, 1, 80, false, new OnSaveListener() {
//            public void onSave(int error, String errorMsg, String path) {
//                WaitingSpinner.dismiss();
//                if (error == 0) {
//                    SQLiteDatabase db = new TemplateDbHelper(CreateOutlineActivity.this).getReadableDatabase();
//                    TemplateDbHelper.insert(db, template);
//                    db.close();
//                    CreateOutlineActivity.this.setResult(-1);
//                    Stat.onEvent(CreateOutlineActivity.this, Stat.ID_CREATE_OUTLINE_FINISH);
//                    CreateOutlineActivity.this.finish();
//                }
//            }
//        });
//    }
//
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (keyCode != 4) {
//            return super.onKeyUp(keyCode, event);
//        }
//        back();
//        return true;
//    }
//
//    private void back() {
//        AlertDialogFragment.create().setText((int) C0752R.string.quite_create_outline_warning).setCancelIcon(C0752R.drawable.ic_cancel).setOkIcon(C0752R.drawable.ic_finish).setOnAlertListener(new OnAlertListener() {
//            public void onAlertClick(Fragment frag, AlertButton which) {
//                if (which == AlertButton.BTN_OK) {
//                    CreateOutlineActivity.this.finish();
//                }
//            }
//        }).show(getSupportFragmentManager(), "quite_create_alert");
//    }
//
//    public void onVisibilityChanged(boolean visible) {
//        int i = View.GONE;
//        if (this.mInputLayout != null) {
//            this.mInputLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
//            View view = this.mLayoutMaterial;
//            if (!visible) {
//                i = View.VISIBLE;
//            }
//            view.setVisibility(i);
//        }
//        if (visible && this.mInputEdit != null && this.mCurrentTextInfo != null) {
//            String text = this.mInputEdit.getText().toString();
//            if (!TextUtils.isEmpty(text)) {
//                this.mInputEdit.setSelection(text.length());
//            }
//        }
//    }
//
//    @Override
//    public void onRequestFontFocus(AirText.TextInfo textInfo) {
//        int focusPosition = 0;
//        for (int i = 0; i < this.mFonts.size(); i++) {
//            if (this.mFonts.get(i) == textInfo.font) {
//                focusPosition = i;
//                break;
//            }
//        }
//        if (this.mFontAdapter != null) {
//            this.mFontAdapter.setFocusPosition(focusPosition);
//            this.mFontAdapter.notifyDataSetChanged();
//        }
//    }
//
//    public void onRequestInput(Object obj) {
//        if (this.mCurrentTextInfo == null) {
//            this.mCurrentTextInfo = new AirText.TextInfo();
//        }
//        AirText.TextInfo ti = (AirText.TextInfo) obj;
//        this.mCurrentTextInfo.typeface = ti.typeface;
//        this.mCurrentTextInfo.text = ti.text;
//        this.mCurrentTextInfo.font = ti.font;
//        this.mInputEdit.setText(this.mCurrentTextInfo.text);
//        this.mInputEdit.setSelection(this.mCurrentTextInfo.text.length());
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                CreateOutlineActivity.this.showInput();
//            }
//        }, 100);
//    }
//
//    private void showInput() {
//        if (this.mInputLayout != null) {
//            this.mInputLayout.setVisibility(View.VISIBLE);
//            this.mInputLayout.requestFocus();
//            KeyboardHelper.showEditKeyboard(this.mInputEdit);
//        }
//    }

}
