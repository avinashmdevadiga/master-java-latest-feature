# Git and GitHub Features Explained

## ALL Git Features

### 1. Version Control
Git tracks every change made to your files over time, letting you see history, compare versions, and revert if something breaks. It solves the classic problem of `final_v2_ACTUAL.docx`-style manual versioning by recording precise, timestamped snapshots with a full history and author trail.

```bash
git init                     # initialize a new Git repository in the current folder
git add file.txt             # stage a file for the next commit
git commit -m "Initial commit"   # save a snapshot of staged changes
git log                      # view commit history
```

### 2. Branching and Merging
A branch is an independent line of development. Branching lets multiple people (or multiple features) evolve in parallel without disturbing the stable `main` branch. Merging brings those changes back together.

```bash
git branch feature-login          # create a new branch
git checkout -b feature-login     # create AND switch to it in one step
git switch main                   # (modern equivalent of checkout) switch back to main
git merge feature-login           # merge feature-login into the current branch
```

**Why it matters:** Without branching, every change would have to go directly into the shared codebase — one bug or half-finished feature would block everyone else.

### 3. Staging Area
The staging area (a.k.a. "the index") is a middle step between your working directory and a commit. It lets you choose exactly *which* changes go into the next commit, instead of committing everything blindly.

```bash
git status              # see which files are modified/staged/untracked
git add file1.txt        # stage only file1.txt
git add .                 # stage everything in the current directory
git reset file1.txt      # unstage file1.txt (keeps the edits)
```

**Why it matters:** You can split unrelated changes into clean, separate commits — e.g., commit a bug fix separately from a half-done feature, even if you edited both at the same time.

### 4. Distributed System
Git is a **distributed** version control system (DVCS) — unlike older tools like CVS or SVN (centralized), every developer has a **full copy of the entire repository history** on their own machine, not just the latest snapshot.

**What this means in practice:**
- You can commit, branch, view history, and diff completely **offline**.
- There's no single point of failure — if the central server goes down, every clone is a complete backup.
- Pushing/pulling is just syncing between two equal copies of the same repository, not a client talking to a master database.

---

## GitHub Features

### 1. Remote Repository Hosting
GitHub hosts your Git repository online so it can be shared, backed up, and accessed by a team from anywhere. A "remote" is simply a named pointer to that hosted repository.

```bash
git remote add origin https://github.com/username/repo.git   # link your local repo to GitHub
git push -u origin main       # push your main branch to GitHub (and set it as the default upstream)
git pull origin main          # pull the latest changes from GitHub into your local branch
```

### 2. Pull Requests (PR)
A Pull Request is GitHub's mechanism for proposing changes from one branch (often a feature branch or a fork) into another (usually `main`), with a review step in between.

**Typical workflow:**
1. Create a branch and commit your changes (`git checkout -b feature-x`).
2. Push the branch to GitHub (`git push origin feature-x`).
3. Open a Pull Request comparing `feature-x` → `main` on GitHub's web UI.
4. Teammates review the diff, leave comments, and request changes.
5. CI checks (tests, linting) run automatically against the PR.
6. Once approved and passing, the PR is merged into `main` (merge, squash, or rebase) and the branch is usually deleted.

**Why it matters:** PRs enforce code review and quality gates before anything reaches the shared branch — critical for team collaboration and audit trails.

### 3. Issues and Project Boards
- **Issues** are GitHub's built-in tracker for bugs, tasks, and feature requests — each has a title, description, labels, assignees, and a comment thread.
- **Project boards** (Kanban-style, e.g., To Do / In Progress / Done) let teams visually organize issues and pull requests, track sprint progress, and link work items directly to the code that resolves them (e.g., "Closes #42" in a commit message auto-closes that issue on merge).

### 4. GitHub Actions (CI/CD Automation)
GitHub Actions lets you define automated workflows — triggered by events like a push, PR, or schedule — using a YAML file stored in `.github/workflows/`.

```yaml
name: CI
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: '21'
      - name: Build with Maven
        run: mvn clean install
```

**Why it matters:** Every push or PR can automatically run tests, build artifacts, deploy to staging, or run security scans — removing manual, error-prone release steps. This is the backbone of modern CI/CD pipelines.

---

## Git vs GitHub Table

| Aspect | Git | GitHub |
|---|---|---|
| **Type** | A version control **tool/software** (command-line) | A **cloud hosting platform** built around Git |
| **Storage** | Local — full repo history lives on your machine | Remote — hosts repos on GitHub's servers (with local clones still existing via Git) |
| **Collaboration** | No built-in collaboration UI; sharing requires manually exchanging patches/bundles or a shared remote | Built for collaboration: Pull Requests, code review, Issues, project boards, team permissions |
| **Installation** | Installed locally on your machine | Accessed via a web browser (or GitHub CLI/Desktop) — no install needed to use it |
| **CI/CD** | Not included — Git itself has no automation | GitHub Actions provides built-in CI/CD |
| **Ownership** | Open-source, created by Linus Torvalds (2005) | Owned by Microsoft (acquired 2018) |
| **Alternatives** | Mercurial, SVN | GitLab, Bitbucket, Azure DevOps |

---

## Git Commands Cheat Sheet

| Command | Purpose |
|---|---|
| `git init` | Initialize a new local repository |
| `git clone <url>` | Copy a remote repository to your local machine |
| `git status` | Show current branch, staged, unstaged, and untracked files |
| `git add <file>` / `git add .` | Stage a file / stage all changes |
| `git commit -m "message"` | Save staged changes as a new commit |
| `git log` / `git log --oneline` | View commit history |
| `git branch` | List branches |
| `git checkout -b <branch>` / `git switch -c <branch>` | Create and switch to a new branch |
| `git merge <branch>` | Merge a branch into the current branch |
| `git rebase <branch>` | Reapply commits on top of another branch |
| `git remote add origin <url>` | Link a local repo to a remote repository |
| `git push origin <branch>` | Upload local commits to the remote |
| `git pull origin <branch>` | Fetch and merge remote changes into your local branch |
| `git fetch` | Download remote changes without merging them |
| `git diff` | Show unstaged changes |
| `git stash` / `git stash pop` | Temporarily shelve changes / restore them |
| `git reset <file>` | Unstage a file (keep the edits) |
| `git reset --hard <commit>` | Discard all changes back to a specific commit |
| `git revert <commit>` | Create a new commit that undoes a previous commit |
| `git tag <name>` | Mark a specific commit (e.g., a release version) |
| `git cherry-pick <commit>` | Apply a specific commit from another branch |

---

## Interview Questions with Answers

**Q1. What is Git and why is it used?**
A: Git is a distributed version control system that tracks changes to files over time, allowing multiple developers to work on the same codebase concurrently without overwriting each other's work. It's used because it enables branching for parallel feature development, keeps a full auditable history, allows offline work (since every clone has the complete history), and supports safe collaboration through merging and conflict resolution.

**Q2. Difference between `git pull` and `git fetch`?**
A: `git fetch` downloads the latest commits, branches, and tags from the remote into your local repository **without** touching your working directory or current branch — it just updates your local view of the remote (e.g., `origin/main`). `git pull` does a `fetch` **followed by** a `merge` (or rebase, with `git pull --rebase`) into your current branch automatically. `fetch` is the "safe, look-before-you-leap" option; `pull` is the "just get me up to date" shortcut.

**Q3. What is a merge conflict and how do you resolve it?**
A: A merge conflict happens when Git can't automatically reconcile changes because the same lines in the same file were modified differently on two branches being merged. Git pauses the merge and marks the conflicting sections in the file with `<<<<<<<`, `=======`, and `>>>>>>>` markers. To resolve it: open the file, manually edit it to keep the correct/combined content, remove the conflict markers, then run `git add <file>` to mark it resolved, and finally `git commit` to complete the merge (or `git merge --continue`).

**Q4. Difference between Git and GitHub?**
A: Git is the underlying version control *tool* — it's software you install locally to track changes and manage branches, and it works entirely offline. GitHub is a *cloud platform* built on top of Git that hosts remote repositories and adds collaboration features on top — Pull Requests, code review, Issues, project boards, and CI/CD via GitHub Actions. You can use Git without ever touching GitHub, but GitHub requires Git underneath it.

**Q5. Explain rebase vs merge.**
A: Both integrate changes from one branch into another, but they produce different history shapes. `git merge` creates a new "merge commit" that ties two branch histories together, preserving exactly how development happened (including all the branching). `git rebase` instead takes your branch's commits and **replays** them on top of the target branch's latest commit, producing a clean, linear history as if you'd branched off the latest code from the start. Trade-off: merge preserves true history and is safer for shared/public branches; rebase produces a cleaner log but rewrites commit history, so it should be avoided on branches others are already working from.

**Q6. How do you undo a commit?**
A: It depends on whether the commit was already pushed/shared:
- **Not yet pushed, want to edit the last commit:** `git commit --amend`
- **Not yet pushed, want to undo but keep the changes staged/unstaged:** `git reset --soft HEAD~1` (keeps changes staged) or `git reset HEAD~1` (keeps changes unstaged)
- **Not yet pushed, want to fully discard it:** `git reset --hard HEAD~1`
- **Already pushed / shared with others:** use `git revert <commit-hash>`, which creates a *new* commit that undoes the changes, without rewriting shared history — the safe option for public branches.

---

## Conclusion

Git and GitHub solve two different but complementary problems. **Git** is the engine of version control itself — it lets any developer track history, branch freely, stage changes precisely, and work fully offline thanks to its distributed design, making it the foundation almost all modern software teams build on. **GitHub** takes that foundation and turns it into a collaboration platform — hosting repositories in the cloud, enabling structured code review through Pull Requests, organizing work with Issues and project boards, and automating testing/deployment through GitHub Actions. Together, they form the backbone of modern software development: Git gives you control over *how code changes*, and GitHub gives you control over *how teams work together* on that code — a combination that's essentially mandatory knowledge for any developer today, and a near-guaranteed topic in technical interviews.