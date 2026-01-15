# Elemental Dragon Documentation

This directory contains the GitHub Pages documentation site for the Elemental Dragon plugin.

## 🌐 Live Documentation

Once deployed, the documentation will be available at:
https://cavarest.github.io/elemental-dragon/

## 📁 Structure

```
docs/
├── _config.yml          # Jekyll configuration
├── Gemfile              # Ruby dependencies
├── .gitignore           # Git ignore rules for Jekyll
├── index.md             # Home page
├── user/                # User documentation
│   └── README.md        # Player guide
├── admin/               # Admin documentation
│   ├── commands.md      # Admin commands reference
│   ├── testing.md       # Testing guide
│   ├── docker.md        # Docker setup
│   └── cicd.md          # CI/CD guide
└── dev/                 # Developer documentation
    └── testing.md       # Developer testing guide
```

## 🚀 Deployment

The documentation is automatically built and deployed via GitHub Actions when:
- Changes are pushed to the `main` branch in the `docs/` directory
- Manually triggered via workflow dispatch

See `.github/workflows/github-pages.yml` for the deployment workflow.

## 🛠️ Local Development

### Prerequisites

- Ruby 3.2+
- Bundler

### Setup

```bash
cd docs
bundle install
```

### Build

```bash
# Build the site
bundle exec jekyll build

# Serve locally with live reload
bundle exec jekyll serve

# The site will be available at http://localhost:4000/elemental-dragon/
```

### Clean

```bash
bundle exec jekyll clean
```

## 📝 Adding Documentation

### Creating a New Page

1. Create a new `.md` file in the appropriate directory (`user/`, `admin/`, or `dev/`)
2. Add front matter at the top:

```yaml
---
layout: default
title: Your Page Title
nav_order: 5
parent: Parent Page (optional)
permalink: /your/page/url/
---
```

3. Write your content in Markdown

### Navigation

Pages are automatically added to the navigation based on:
- `nav_order`: Determines the order in the menu
- `parent`: Creates hierarchical navigation
- `title`: Appears in the navigation menu

## 🎨 Theme

This site uses the [Just the Docs](https://just-the-docs.com/) theme, which provides:
- Clean, responsive design
- Built-in search
- Syntax highlighting
- Easy navigation
- Mobile-friendly layout

## 📚 Documentation Guidelines

### Writing Style

- Use clear, concise language
- Include code examples where appropriate
- Add command syntax and examples
- Use callouts for important information

### Callouts

```markdown
{: .note }
This is a note callout

{: .warning }
This is a warning callout

{: .important }
This is an important callout
```

### Code Blocks

Use fenced code blocks with language specification:

````markdown
```bash
/lightning 1
```

```java
public class Example {
    // Java code here
}
```
````

## 🔧 Configuration

Key configuration options in `_config.yml`:

- `title`: Site title
- `description`: Site description
- `url`: Base URL (https://cavarest.github.io)
- `baseurl`: Repository path (/elemental-dragon)
- `theme`: Jekyll theme (just-the-docs)

## 📦 Collections

The site uses Jekyll collections for organization:

- `user`: User documentation
- `admin`: Administrator documentation
- `dev`: Developer documentation

## 🤝 Contributing

When updating documentation:

1. Make changes in the `docs/` directory
2. Test locally with `bundle exec jekyll serve`
3. Commit and push to the `main` branch
4. GitHub Actions will automatically build and deploy

## 📄 License

Documentation is licensed under MIT License, same as the plugin.