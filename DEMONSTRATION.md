# Demostración del Problema y Solución / Problem and Solution Demonstration

## Prueba con bibliography.bib (✅ CORRECTO / CORRECT)

### Comando / Command:
```bash
pandoc paper.md --bibliography=bibliography.bib --citeproc -o paper.html
```

### Resultado / Result:
✅ **SUCCESS**: Las citas se procesan correctamente / Citations are processed correctly

El archivo HTML generado muestra:
- Citas en el texto con el formato apropiado: `(Albaladejo González et al. 2024)`
- Lista de referencias completa al final del documento
- Todos los metadatos de las publicaciones correctamente formateados

*The generated HTML file shows:*
- *Citations in the text with proper format: `(Albaladejo González et al. 2024)`*
- *Complete reference list at the end of the document*
- *All publication metadata correctly formatted*

---

## Prueba con bibliography.py (❌ INCORRECTO / INCORRECT)

### Comando / Command:
```bash
pandoc paper.md --bibliography=bibliography.py --citeproc -o paper.html
```

### Resultado / Result:
❌ **ERROR**: `Could not determine bibliography format for bibliography.py`

Pandoc no puede procesar el archivo porque no reconoce el formato `.py` como un formato de bibliografía válido.

*Pandoc cannot process the file because it doesn't recognize the `.py` format as a valid bibliography format.*

---

## Formatos de Bibliografía Soportados / Supported Bibliography Formats

Pandoc soporta los siguientes formatos de bibliografía:

*Pandoc supports the following bibliography formats:*

| Formato / Format | Extensión / Extension | Descripción / Description |
|------------------|----------------------|---------------------------|
| BibTeX | `.bib` | Formato estándar para LaTeX / Standard format for LaTeX |
| BibLaTeX | `.bib` | Versión extendida de BibTeX / Extended version of BibTeX |
| CSL JSON | `.json` | Citation Style Language JSON format |
| CSL YAML | `.yaml` | Citation Style Language YAML format |
| RIS | `.ris` | Research Information Systems format |
| EndNote | `.enl` | EndNote library format |

## Conclusión / Conclusion

Para que las citas funcionen correctamente en `paper.md`, **debe** usar `bibliography.bib` con formato BibTeX, **no** `bibliography.py`.

*For citations to work correctly in `paper.md`, you **must** use `bibliography.bib` with BibTeX format, **not** `bibliography.py`.*

### Pasos para corregir el problema / Steps to fix the problem:

1. Renombrar o convertir `bibliography.py` a `bibliography.bib`
   *Rename or convert `bibliography.py` to `bibliography.bib`*

2. Asegurar que el contenido esté en formato BibTeX válido
   *Ensure the content is in valid BibTeX format*

3. Actualizar la referencia en el encabezado YAML de `paper.md`:
   *Update the reference in the YAML header of `paper.md`:*
   ```yaml
   bibliography: bibliography.bib
   ```

4. Compilar el documento con pandoc:
   *Compile the document with pandoc:*
   ```bash
   pandoc paper.md --bibliography=bibliography.bib --citeproc -o paper.pdf
   ```
