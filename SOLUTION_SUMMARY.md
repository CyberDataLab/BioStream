# Resumen de la Solución / Solution Summary

## Problema Original / Original Problem

**Español:** Las citas en `paper.md` no se cargan correctamente cuando la bibliografía está almacenada en `bibliography.py`.

**English:** Citations in `paper.md` are not loading correctly when the bibliography is stored in `bibliography.py`.

---

## Causa Raíz / Root Cause

Los procesadores de citas para documentos Markdown (como `pandoc-citeproc`) **solo reconocen formatos estándar de bibliografía**:

*Citation processors for Markdown documents (like `pandoc-citeproc`) **only recognize standard bibliography formats**:*

- ✅ BibTeX (`.bib`)
- ✅ CSL JSON (`.json`)
- ✅ CSL YAML (`.yaml`)
- ✅ RIS (`.ris`)
- ❌ **NO** Python (`.py`)

---

## Solución Implementada / Implemented Solution

### 1. Archivos Creados / Files Created

#### `bibliography.bib` ✅
Archivo de bibliografía en formato BibTeX correcto. Este es el formato que pandoc puede procesar.

*Bibliography file in correct BibTeX format. This is the format that pandoc can process.*

```bibtex
@article{clave2024,
  title={Título del artículo},
  author={Autor, Nombre},
  journal={Nombre de la revista},
  year={2024}
}
```

#### `paper.md` ✅
Documento de ejemplo configurado correctamente para usar `bibliography.bib`:

*Example document correctly configured to use `bibliography.bib`:*

```yaml
---
title: 'BioStream: An open-source solution...'
bibliography: bibliography.bib
---
```

#### `bibliography.py` ❌
Ejemplo del formato **incorrecto** (incluido solo con fines demostrativos).

*Example of the **incorrect** format (included for demonstration purposes only).*

### 2. Documentación / Documentation

- **CITATION_GUIDE.md**: Guía completa bilingüe sobre cómo usar correctamente las citas
  *Complete bilingual guide on how to properly use citations*

- **DEMONSTRATION.md**: Pruebas que demuestran la diferencia entre el formato correcto e incorrecto
  *Tests demonstrating the difference between correct and incorrect formats*

- **.gitignore**: Excluye archivos generados (PDF, HTML, DOCX)
  *Excludes generated files (PDF, HTML, DOCX)*

---

## Verificación / Verification

### ✅ Prueba Exitosa / Successful Test

```bash
pandoc paper.md --bibliography=bibliography.bib --citeproc -o paper.html
```

**Resultado / Result:** 
- Las citas se procesan correctamente en el texto: `(Albaladejo González et al. 2024)`
- Las referencias se generan automáticamente al final del documento
- *Citations are processed correctly in text: `(Albaladejo González et al. 2024)`*
- *References are automatically generated at the end of the document*

### ❌ Prueba Fallida / Failed Test

```bash
pandoc paper.md --bibliography=bibliography.py --citeproc -o paper.html
```

**Error:** `Could not determine bibliography format for bibliography.py`

---

## Cómo Usar / How to Use

### Paso 1: Preparar la bibliografía / Step 1: Prepare the bibliography

Crear o mantener el archivo `bibliography.bib` con entradas en formato BibTeX.

*Create or maintain the `bibliography.bib` file with entries in BibTeX format.*

### Paso 2: Configurar paper.md / Step 2: Configure paper.md

Asegurar que el encabezado YAML incluya:

*Ensure the YAML header includes:*

```yaml
bibliography: bibliography.bib
```

### Paso 3: Usar citas en el texto / Step 3: Use citations in text

Referenciar las citas usando la sintaxis: `[@clave]`

*Reference citations using the syntax: `[@clave]`*

```markdown
Este es un hecho importante [@smith2023].
*This is an important fact [@smith2023].*
```

### Paso 4: Compilar el documento / Step 4: Compile the document

```bash
# Generar PDF
pandoc paper.md --bibliography=bibliography.bib --citeproc -o paper.pdf

# Generar HTML
pandoc paper.md --bibliography=bibliography.bib --citeproc -o paper.html

# Generar DOCX
pandoc paper.md --bibliography=bibliography.bib --citeproc -o paper.docx
```

---

## Recursos Adicionales / Additional Resources

- [Pandoc Manual - Citations](https://pandoc.org/MANUAL.html#citations)
- [BibTeX Format Documentation](http://www.bibtex.org/Format/)
- [JOSS Submission Guidelines](https://joss.readthedocs.io/en/latest/submitting.html)
- [Citation Style Language](https://citationstyles.org/)

---

## Conclusión / Conclusion

**Español:** Para que las citas funcionen en documentos Markdown, la bibliografía **debe** estar en formato BibTeX (`.bib`), no en formato Python (`.py`). El problema se ha resuelto proporcionando el archivo `bibliography.bib` correcto y documentación completa.

**English:** For citations to work in Markdown documents, the bibliography **must** be in BibTeX format (`.bib`), not Python format (`.py`). The problem has been resolved by providing the correct `bibliography.bib` file and comprehensive documentation.
