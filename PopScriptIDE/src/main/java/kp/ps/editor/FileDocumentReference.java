/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.editor;

import java.nio.file.Path;

/**
 *
 * @author Marc
 */
public interface FileDocumentReference
{
    Path getFilePath();
    
    default boolean hasFile() { return getFilePath() != null; }
}
