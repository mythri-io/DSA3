# Cybersecurity Conversation Threat Detection

A Java + HTML/CSS/JavaScript DSA project that analyzes **authorized text conversations**
for cybersecurity indicators. The primary algorithm is Aho–Corasick multi-pattern matching.

## Features
- Aho–Corasick Trie with BFS-built failure links
- Conversation-level aggregation, matched phrases, counts, and character positions
- CSV conversation dataset and threat-pattern catalog
- Local web dashboard with text analysis and security-review alerts
- KMP, Rabin–Karp, and Z-algorithm reference implementations
- Synthetic dataset generation script is not required to run the application
- Report output from the command-line runner

> The included conversations are synthetic demonstration data. Keyword matches are indicators,
> not proof of malicious intent. Use only data you are authorized to analyze and route alerts
> to a human reviewer.

## Requirements
- JDK 17 or later
- No third-party Java dependencies

## Run the command-line analysis
From the project root:

```bash
javac --add-modules jdk.httpserver -d bin src/*.java
java --add-modules jdk.httpserver -cp bin Main
```

## Run the web dashboard
```bash
java --add-modules jdk.httpserver -cp bin WebServer
```
Then open http://localhost:8080

The server serves `web/` and exposes:
- `GET /api/summary`
- `POST /api/analyze` with JSON body `{"text":"..."}`
- `POST /api/rescan` to analyze the bundled dataset

## Dataset format
`data/conversations.csv` columns:
`conversation_id,message_id,sender,receiver,timestamp,message`

`data/threat_patterns.csv` columns:
`pattern,category,severity`

## Project structure
- `src/` Java source code
- `data/` synthetic conversation corpus and pattern catalog
- `web/` browser dashboard
- `reports/` generated text report
