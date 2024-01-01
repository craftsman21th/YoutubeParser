package com.moder.compass.ui.widget;

import android.graphics.drawable.Drawable;

/**
 * 菜单项。作为菜单的具体内容，每个菜单项会包含几项内容：id(required),content(optional),icon(optional)。 其中id作为唯一标识，用于匹配点击项。content是菜单项的文字内容。
 * icon是菜单项的图标。
 * <p>
 * 作为PopupMenu的内部类，需要通过PopupMenu的实例来调用构造函数。 <br>
 * Sample:
 *
 * <pre>
 * PopupMenu menu = new PopupMenu(mContext);
 * PopupMenuItem menuItem = menu.new PopupMenuItem(0, xxx);
 * </pre>
 *
 * com.dubox.drive.ui.widget.PopupMenuItem
 *
 * @author 文超 <br/>
 *         create at 2014-2-26 上午11:58:39
 */
public class PopupMenuItem {
    public int id;
    public String content;
    public Drawable icon;
    public Drawable afterIcon;
    public boolean selected;
    /** 是否需要展示新功能提示（红点） */
    public boolean needShowBadgeNewFunc;

    public PopupMenuItem(int id, String content) {
        this(id, content, null);
    }

    public PopupMenuItem(int id, Drawable icon) {
        this(id, null, icon);
    }

    public PopupMenuItem(int id, String content, Drawable icon) {
        this(id, content, icon, false);
    }

    public PopupMenuItem(int id, String content, Drawable icon, boolean selected) {
        this(id, content, icon, null, selected, false);
    }

    public PopupMenuItem(int id, String content, Drawable icon, Drawable afterIcon, boolean selected,
            boolean showBadgeNewFunc) {
        this.id = id;
        this.content = content;
        this.icon = icon;
        this.afterIcon = afterIcon;
        this.selected = selected;
        this.needShowBadgeNewFunc = showBadgeNewFunc;
    }
}
