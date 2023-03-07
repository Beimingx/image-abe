/*
 * MATLAB Compiler: 8.5 (R2022b)
 * Date: Mon Mar  6 22:01:46 2023
 * Arguments: 
 * "-B""macro_default""-W""java:imageprocessor,ImageProcessor""-T""link:lib""-d""D:\\dcpabe\\imageprocessor\\for_testing""class{ImageProcessor:D:\\dcpabe\\matlab-code\\keyGenrate.m,D:\\dcpabe\\matlab-code\\LabelBox.m,D:\\dcpabe\\matlab-code\\predict.m,D:\\dcpabe\\matlab-code\\Reciver.m,D:\\dcpabe\\matlab-code\\Sender.m}"
 */

package imageprocessor;

import com.mathworks.toolbox.javabuilder.*;
import com.mathworks.toolbox.javabuilder.internal.*;
import java.io.Serializable;
/**
 * <i>INTERNAL USE ONLY</i>
 */
public class ImageprocessorMCRFactory implements Serializable 
{
    /** Component's uuid */
    private static final String sComponentId = "imageprocess_4d6d650f-0848-4c46-8115-db0c7eac07f1";
    
    /** Component name */
    private static final String sComponentName = "imageprocessor";
    
   
    /** Pointer to default component options */
    private static final MWComponentOptions sDefaultComponentOptions = 
        new MWComponentOptions(
            MWCtfExtractLocation.EXTRACT_TO_CACHE, 
            new MWCtfClassLoaderSource(ImageprocessorMCRFactory.class)
        );
    
    
    private ImageprocessorMCRFactory()
    {
        // Never called.
    }
    
    public static MWMCR newInstance(MWComponentOptions componentOptions) throws MWException
    {
        if (null == componentOptions.getCtfSource()) {
            componentOptions = new MWComponentOptions(componentOptions);
            componentOptions.setCtfSource(sDefaultComponentOptions.getCtfSource());
        }
        return MWMCR.newInstance(
            componentOptions, 
            ImageprocessorMCRFactory.class, 
            sComponentName, 
            sComponentId,
            new int[]{9,13,0}
        );
    }
    
    public static MWMCR newInstance() throws MWException
    {
        return newInstance(sDefaultComponentOptions);
    }
}
