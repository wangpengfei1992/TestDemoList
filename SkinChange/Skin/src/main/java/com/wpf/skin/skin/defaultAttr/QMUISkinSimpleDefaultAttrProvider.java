/*
 * Tencent is pleased to support the open source community by making QMUI_Android available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wpf.skin.skin.defaultAttr;

import androidx.collection.SimpleArrayMap;

public class QMUISkinSimpleDefaultAttrProvider implements IQMUISkinDefaultAttrProvider {

    private SimpleArrayMap<String, Integer> mSkinAttrs = new SimpleArrayMap<>();

    public void setDefaultSkinAttr(String name, int attr) {
        mSkinAttrs.put(name, attr);
    }

    @Override
    public SimpleArrayMap<String, Integer> getDefaultSkinAttrs() {
        return mSkinAttrs;
    }
}
