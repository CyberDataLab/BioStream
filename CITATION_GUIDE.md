# Solución al Problema de Citas / Citation Problem Solution

## Problema / Problem

Las citas en `paper.md` no se cargan correctamente cuando la bibliografía está en `bibliography.py`.

*Citations in `paper.md` are not loading correctly when the bibliography is in `bibliography.py`.*

## Causa / Cause

Los procesadores de citas para Markdown (como `pandoc-citeproc`) requieren que la bibliografía esté en formato **BibTeX** (archivo `.bib`), no en un archivo Python (`.py`).

*Citation processors for Markdown (such as `pandoc-citeproc`) require the bibliography to be in **BibTeX** format (`.bib` file), not in a Python file (`.py`).*

## Solución / Solution

### 1. Usar el archivo correcto / Use the correct file

El archivo de bibliografía debe ser:
- ✅ `bibliography.bib` (formato BibTeX)
- ❌ `bibliography.py` (formato Python)

*The bibliography file should be:*
- *✅ `bibliography.bib` (BibTeX format)*
- *❌ `bibliography.py` (Python format)*

### 2. Formato BibTeX / BibTeX Format

El archivo `bibliography.bib` debe contener entradas en formato BibTeX:

```bibtex
@article{clave2024,
  title={Título del artículo},
  author={Autor, Nombre},
  journal={Nombre de la revista},
  year={2024},
  doi={10.xxxx/xxxxx}
}
```

### 3. Referenciar en paper.md / Reference in paper.md

En el encabezado YAML de `paper.md`, especificar:

```yaml
---
title: 'Título del paper'
bibliography: bibliography.bib
---
```

Dentro del texto, usar citas como: `[@clave2024]`

*In the YAML header of `paper.md`, specify:*
*Within the text, use citations like: `[@clave2024]`*

### 4. Compilar el documento / Compile the document

Para generar el PDF con las citas correctamente formateadas:

```bash
pandoc paper.md --bibliography=bibliography.bib --citeproc -o paper.pdf
```

*To generate the PDF with correctly formatted citations:*

## Archivos de ejemplo / Example files

- `paper.md` - Archivo de ejemplo con citas / Example file with citations
- `bibliography.bib` - Bibliografía en formato BibTeX correcto / Bibliography in correct BibTeX format
- `bibliography.py` - ❌ Formato incorrecto (solo para referencia) / Incorrect format (for reference only)

## Comandos útiles / Useful commands

### Generar PDF / Generate PDF
```bash
pandoc paper.md --bibliography=bibliography.bib --citeproc -o paper.pdf
```

### Generar HTML / Generate HTML
```bash
pandoc paper.md --bibliography=bibliography.bib --citeproc -o paper.html
```

### Generar DOCX / Generate DOCX
```bash
pandoc paper.md --bibliography=bibliography.bib --citeproc -o paper.docx
```

## Recursos adicionales / Additional resources

- [Pandoc Citation Syntax](https://pandoc.org/MANUAL.html#citations)
- [BibTeX Format](http://www.bibtex.org/Format/)
- [JOSS Paper Guidelines](https://joss.readthedocs.io/en/latest/submitting.html)

## Nota importante / Important note

El archivo `bibliography.py` ha sido incluido solo para demostrar el formato **incorrecto**. En un proyecto real, este archivo debe ser eliminado o renombrado a `bibliography.bib` con el formato BibTeX apropiado.

*The `bibliography.py` file has been included only to demonstrate the **incorrect** format. In a real project, this file should be deleted or renamed to `bibliography.bib` with the appropriate BibTeX format.*
