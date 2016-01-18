# Commit message guidelines #

### Introduction ###
We should use very precise rules over how our commit messages can be formatted. This leads to more readable messages that are easy to follow when looking through the project history.


### Details ###
Each commit message consists of a **header**, a **note**. The **header** has a special format that includes a **type**, a **scope** and a **subject**:

```
<type>(<scope>): <subject>
<BLANK LINE>
<note>
```

---

**Type**:

  * feat: A new feature
  * fix: A bug fix
  * docs: Documentation only changes
  * refactor: A code change that neither fixes a bug or adds a feature
  * test: Adding missing tests
  * merge: Merging other's code

---

**Scope**:

  * UI
  * CC
  * TDM
  * TFM
  * DAO
  * OTHERS

---

**Subject**:

  * use the imperative, present tense: "change" not "changed" nor "changes"
  * don't capitalize first letter
  * no dot (.) at the end

---

**Note**:

No strict rules here, just add any additional note for others.