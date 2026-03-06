一个从零开始打造的 Minecraft Forge Mod。  
起点是简单的原版增强，终点是属于自己的世界观、武器体系和维度。

---
First Stage:基于主世界的原版增强
## ✨ 当前进度 阶段:[*     ] (4%)

- [√] 第一个物品与尝试
- [going] 建立基础模组结构
- [√] 添加第一件武器：超级钻石剑（Super Diamond Sword）
- [going] 自定义体系与物品注册
- [ ] 自定义矿物、材料体系
- [going ] 自定义工具 Tier（材料等级）
- [going ] 原版增强内容（合成、附魔、特效等）
- [ ] 第一个维度（FoWorld）
- [ ] 新生物、新结构、新 Boss

### 魔法体系 进度[*  ] (1%)
- [√] 第一件魔法物品：荧光粉尘
- [√] 第一件魔法武器：辉光钻石镐
- [going] 第一件体系物品：荧光花

---

## 🛠️ 技术栈



---

## 📁 版本记录
2026.1.6    version1.0.2 熟悉流程和ui，踩坑与部分思考
            学习了容器的能力系统，更加熟悉整体机械流程和相关功能调用，熟悉了注册，用法，学会了设计GUI界面。
            新增石制碎石机，修复破坏不掉落，增加多副产物seralizer,漏斗输入输出，新增一个配方。
            修改了贴图ui，修改了超级钻石镐和超级钻石剑的命名，下调属性

2026.1.7    version1.0.3 学习NBT和Tags，熟悉事件，世界相关概念
            对贴图，路径有更深刻的认识，学习了tags和nbt为item或其他定制属性。
            制作“辉光钻石镐”：可以右键切换辉光模式，若半径为5内有稀有矿物则挖掘出现粒子效果。
            完善合成体系，思考下一步的进程。

2026.1.8    version1.0.4 

2026.1.12   version1.0.5 / version1.0.6 
            地下植物-伶幽兰
            荧光坟墓-3d测试

2026.1.13   version1.0.7
            第一个特效+属性剑 枯光石刃-由破坏荧光坟墓召唤的旧光埋葬者召唤
            枯光石刃：攻击三次为自己提供一个抗性提升属性，并给予敌人缓慢效果，额外消耗一点耐久

2026.1.15   version1.0.8
            制作多种符石，尝试使用批量生成
            平衡荧光坟墓的骷髅属性


---
## 🌍 天光群系定位问题排查（1.20.1）
- 现象 1：在主世界使用 `/locate biome foworld:skylight_forest` 时提示“无法在合理的距离找到群系”。
- 根因 1：当前工程没有把 `foworld:skylight_forest` 注入主世界噪声群系分布；`forge:add_features` 只能给“已存在群系”加地物，不会新增群系。
- 现象 2：将世界设为单一天光群系时崩溃，报错 `Feature order cycle found`。
- 根因 2（最终定位）：`foworld:skylight_tree_placed` 同时被两个 biome modifier 注入到同一群系（`#minecraft:is_overworld` 和 `#foworld:is_skylight_forest`），在同一步骤与其他地物混排后形成前后顺序环，触发 feature sorter 循环依赖。
- 修复：移除 `add_skylight_tree_to_overworld.json`，避免对同一群系重复注入 `skylight_tree_placed`。
- 结论：
  - 天光群系“在主世界无法 locate”是设计现状（需 TerraBlender 等方案才能注入主世界）。
  - 单一天光群系崩溃问题已通过移除重复地物注入修复。

## 🧭 天光群系创建与排错方法（给协作 AI）
1. **先保证群系会被实际装饰**：
   - 在 `forge biome_modifier` 里给 `#foworld:is_skylight_forest` 注入植被地物（树、草、花）。
   - 推荐把原版稳定地物先加上（如 `minecraft:trees_plains`、`minecraft:flower_default`、`minecraft:patch_grass_plain`），再叠加自定义地物。
2. **树木放置规则要可落地**：
   - `placed_feature` 使用 `count_extra + in_square + heightmap + biome` 组合，确保尝试点位在地表。
   - 避免使用会把绝大多数尝试点过滤掉的 placement 规则。
3. **地下特征与植被特征分阶段**：
   - 矿石类放 `underground_ores`。
   - 树木/花草放 `vegetal_decoration`。
4. **避免 feature order cycle**：
   - 不要把同一个 placed feature 通过多个 modifier 对同一群系重复注入。
   - 发现 `Feature order cycle found` 时，先查重注入，再查阶段是否错放。
5. **最小验证流程**：
   - `Single Biome` 选择 `foworld:skylight_forest` 启动；
   - 观察地表是否有树、草、花；
   - 下探确认 `deep_glow_stone` 是否生成；
   - 控制台无 `Feature order cycle found`。

## 🌲 天光木群系（Skylight Forest）创建方法（协作标准）
目标：构建“**大型天光树为主 + 小概率辉光坟墓 + 草方块/暗光荧石地表点缀 + 伶幽兰与其他花**”的稳定群系。

### 1) 特征拆分（避免循环依赖）
- `vegetal_decoration`：树木与花草（`skylight_tree_placed`、`skylight_surface_orchid_placed`、vanilla 花草树）。
- `local_modifications`：地表暗光荧石斑块（`skylight_surface_glowstone_patch_placed`）。
- `surface_structures`：小概率辉光坟墓（`skylight_tomb_placed`）。
- `underground_ores`：地下暗光荧石矿脉（`deep_glow_stone_placed`）。

### 2) 大型天光树实现要点
- 自定义 `skylight_tree` 特征提高树高并扩大树冠。
- 树干改用 `skylight_log` / `glow_log` 混合，强化群系识别度。

### 3) 坟墓生成规范
- 自定义 `glow_tomb` 特征在地表生成：
  - 3x3 鉴刻光纹石英基座；
  - 四角暗光荧石点缀；
  - 中央辉光墓碑；
  - 小概率顶部灵魂灯。
- 通过 `rarity_filter` 控制“低概率出现”。

### 4) 最小回归检查
- 单一天光群系开图后：
  - 能看到明显大型天光树群；
  - 可见普通花草与伶幽兰；
  - 地表出现少量暗光荧石斑块；
  - 地表偶发辉光坟墓；
  - 控制台无 `Feature order cycle found`。
