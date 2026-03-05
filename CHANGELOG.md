# FoWorld 未来之征 - 更新日志

## 版本 1.0.0

### 新增内容

#### 天光树系列
- **天光木 (Skylight Log)** - 新的木头类型，可在天光森林群系中找到
- **天光木块 (Skylight Wood)** - 天光木的完整方块版本
- **天光树苗 (Skylight Sapling)** - 可种植生成天光树
  - 单树苗种植：生成高度10-14格的天光树
  - 四树苗2x2种植：生成高度18-26格的大型天光树
  - 金合欢树风格的树枝生成，带有顶部树叶簇
  - 树叶设置为持久状态，不会因距离过远而凋落
- **荧光木 (Glow Log)** - 发光版本的木头，光照等级7
- **荧光木块 (Glow Wood)** - 荧光木的完整方块版本

#### 天光树生成机制
- 木头随机混合生成：
  - 橡木：40%
  - 荧光木：20%
  - 天光木：40%
- 底部和顶部始终为橡木
- 四树苗检测系统，自动识别2x2种植区域

#### 荧光花配方
- 荧光花可将任意原木转化为荧光木
- 消耗5点能量，转化时间约9秒
- 支持所有原版原木类型

#### 天光森林群系
- 新的生物群系：天光森林 (Skylight Forest)
- 群系特性：
  - 温度：0.7
  - 降雨：0.8
  - 分类：森林
- 生物生成：
  - 友好生物：羊、猪、鸡、牛、蝙蝠
  - 敌对生物：蜘蛛、僵尸、骷髅、Creeper
- 地表特征：
  - 深光荧石生成
  - 伶幽兰生成
  - 天光树生成

### 技术细节

#### 标签注册
- `minecraft:logs` - 天光木、荧光木添加到原木标签
- `minecraft:mineable/axe` - 斧头可快速挖掘
- `minecraft:is_overworld` - 天光森林标记为主世界群系
- `minecraft:is_forest` - 天光森林标记为森林类型

#### 汉化支持
- 完整的中英文翻译支持
- 创造标签页名称：FoWorld 未来之征

### 使用方法
- 使用 `/locatebiome foworld:skylight_forest` 定位天光森林群系
- 天光树苗可在创造模式物品栏中获取
- 四个天光树苗放置成2x2可生长为大型天光树

---

## 开发者备注

### 文件结构
```
src/main/java/ii52/FoWorld/
├── biome/
│   └── SkylightBiomes.java          # 群系注册
├── block/sylight/
│   ├── SkylightLogBlock.java        # 天光木方块
│   ├── SkylightWoodBlock.java       # 天光木块方块
│   ├── SkylightSaplingBlock.java    # 天光树苗方块
│   ├── SkylightRegistry.java        # 方块注册
│   └── SkylightTreeGrower.java      # 树木生成器
└── worldgen/
    ├── SkylightTreeFeature.java     # 天光树特征
    └── SkylightFeatures.java        # 特征注册

src/main/resources/data/foworld/
├── worldgen/
│   ├── biome/
│   │   └── skylight_forest.json     # 群系定义
│   ├── configured_feature/
│   │   └── skylight_tree_configured.json
│   └── placed_feature/
│       └── skylight_tree_placed.json
└── forge/biome_modifier/
    └── *.json                       # 群系修饰器
```

### 已知问题
- 旧存档可能因无效的biome modifier类型无法加载，请创建新世界测试
