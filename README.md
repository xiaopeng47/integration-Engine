# mule-replacement-engine

基于 `docs/mule-replacement-plan.md` 的 Java 21 + Maven 初始工程，实现了替代引擎的最小骨架：

- XML 兼容输入：`MuleXmlParser` 解析 Mule app XML 的 flow/sub-flow 子集。
- IR 模型：`AppModel` / `FlowModel` / `NodeModel`。
- Runtime：`EngineRuntime` 执行节点链。
- 内置算子样例：`logger`、`set-variable`。
- 连接器接口骨架：HTTP、JDBC。

## 构建与测试

```bash
mvn test
```

## 启动示例

```bash
mvn -q -DskipTests package
java -jar target/mule-replacement-engine-0.1.0-SNAPSHOT.jar /path/to/app.xml demoFlow
```

> 当前是 Phase-1 skeleton，重点验证 XML -> IR -> Runtime 主链路。
