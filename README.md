# mule-replacement-engine

基于 `docs/mule-replacement-plan.md` 的 Java 21 + Maven 引擎工程，当前实现到 **Phase-1 可执行骨架+扩展点**。

## 已实现能力

- XML 兼容输入：`MuleXmlParser` 支持 `flow/sub-flow`，并支持嵌套节点解析。
- IR 模型：`AppModel` / `FlowModel` / `NodeModel`（含 children）。
- Runtime：`EngineRuntime` 支持流程执行与内联节点执行。
- 表达式：`ExpressionEvaluator` 支持 `#[payload]`、`#[vars.xxx]`、`${vars.xxx}` 插值。
- 处理器 SPI：`Processor` + `ProcessorRegistry`。
- 内置处理器：
  - `logger`
  - `set-variable`
  - `flow-ref`
  - `choice`（when/otherwise）
  - `for-each`
- 连接器接口骨架：HTTP、JDBC。

## 运行计划（迭代）

1. **P1（已完成）**：XML -> IR -> Runtime 主链路 + 常用算子子集。
2. **P2（进行中）**：HTTP Listener/Request、JDBC 执行器接入与错误语义映射。
3. **P3（规划）**：调度、可观测、回归双跑、压测基线。
4. **P4（规划）**：按优先级纳入自研算子（语法与官方一致）。

## 构建与测试

```bash
mvn test
```

## 启动示例

```bash
mvn -q -DskipTests package
java -jar target/mule-replacement-engine-0.1.0-SNAPSHOT.jar /path/to/app.xml demoFlow
```
