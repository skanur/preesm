/*
	============================================================================
	Name        : sobel.c
	Author      : kdesnos
	Version     : 1.2
	Copyright   : CECILL-C
	Description :
	============================================================================
*/

#include <string.h>
#include <stdlib.h>

#include "sobel.h"

//void sobel(int width, int height, int outHeight, IN unsigned char *input, OUT unsigned char *output)
#if 1
void sobel(int width, int height, unsigned char *input, unsigned char *output)
#endif
{
    int i,j;

    // Apply the filter
    for(j=1; j<height-1; j++){
        for(i=1; i<width-1; i++){
            int gx = -input[(j-1)*width + i-1] -2*input[  j*width + i-1] -input[(j+1)*width + i-1]
                     +input[(j-1)*width + i+1] +2*input[  j*width + i+1] +input[(j+1)*width + i+1];
            int gy = -input[(j-1)*width + i-1] -2*input[(j-1)*width + i] -input[(j-1)*width + i+1]
                     +input[(j+1)*width + i-1] +2*input[(j+1)*width + i] +input[(j+1)*width + i+1];

            output[(j-1)*width + i] = (abs(gx) + abs(gy))/8;
        }
    }

    // Fill the left and right sides
    for(j=0; j<height-2 ; j++){
        output[j*width] = 0;
        output[(j+1)*width-1] = 0;
    }
}
