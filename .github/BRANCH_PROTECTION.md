# Configuração de Proteção de Branch

Para proteger completamente a branch `master`, você precisa configurar as regras de proteção no GitHub:

## Passos para configurar no GitHub:

1. Acesse o repositório no GitHub
2. Vá em **Settings** → **Branches**
3. Clique em **Add rule** em "Branch protection rules"
4. Configure as seguintes opções:

### Configurações Recomendadas:

- **Branch name pattern**: `master`
- ✅ **Require a pull request before merging**
  - ✅ Require approvals (mínimo: 1)
  - ✅ Dismiss stale pull request approvals when new commits are pushed
- ✅ **Require status checks to pass before merging**
  - ✅ Require branches to be up to date before merging
  - Adicione o check: `test` (do workflow CI)
- ✅ **Require conversation resolution before merging**
- ✅ **Do not allow bypassing the above settings**
- ✅ **Restrict who can push to matching branches** (opcional)
  - Não adicione ninguém, assim ninguém pode fazer push direto

## O que esta configuração faz:

1. **Bloqueia commits diretos** na master
2. **Exige Pull Requests** para qualquer alteração
3. **Executa testes automaticamente** em todos os PRs
4. **Exige aprovação** antes de fazer merge
5. **Garante que os testes passem** antes do merge

## Workflows configurados:

- **ci.yml**: Executa testes em PRs e pushes em outras branches
- **protect-master.yml**: Bloqueia pushes diretos na master (segurança adicional)

## Uso no dia a dia:

```bash
# 1. Crie uma nova branch para suas alterações
git checkout -b feature/minha-feature

# 2. Faça suas alterações e commits
git add .
git commit -m "Implementa nova feature"

# 3. Envie para o repositório
git push origin feature/minha-feature

# 4. Abra um Pull Request no GitHub
# 5. Aguarde os testes passarem
# 6. Solicite revisão
# 7. Após aprovação, faça o merge
```

## Observações:

- Commits diretos na master serão **rejeitados**
- PRs com testes falhando **não podem ser merged**
- Todo código deve passar pelos testes antes de chegar na master
