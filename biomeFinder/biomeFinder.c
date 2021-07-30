#include "generator.h"
#include "finders.h"
#include <stdio.h>
#include <stdlib.h>

//set seed
uint64_t SEED = 1711874039345768927;

struct Biome
{
  int64_t x;
  int64_t z;
  int16_t biomeID;
};

int main(){

}

int biomesInArea(int minx, int minz, int maxx, int maxz){
  FILE *fptr;
  fptr = fopen("out.bin","wb");
  struct Biome bc;
  LayerStack g;
  unsigned int width = maxx-minx;
  unsigned int height = maxz-minz;
  setupGenerator(&g, MC_1_17);
  Layer *layer = &g.layers[L_SHORE_16];
  setLayerSeed(layer, SEED);
  for(int i=minx;i<=maxx;i++){
    for(int j=minz;j<=maxz;j++){
        Pos pos = {i,j};
        bc.biomeID = getBiomeAtPos(&g, pos);
        bc.x = i;
        bc.z = j;
        fwrite(&bc, sizeof(struct Biome), 1, fptr);
    }
  }
  fclose(fptr);
}

int averageBiomesInArea(int minx, int minz, int maxx, int maxz){
  FILE *fptr;
  fptr = fopen("out.bin","wb");
  struct Biome bc;
  LayerStack g;
  unsigned int width = maxx-minx;
  unsigned int height = maxz-minz;
  setupGenerator(&g, MC_1_17);
  Layer *layer = &g.layers[L_SHORE_16];
  setLayerSeed(layer, SEED);
  for(int i=minx;i<=maxx;i+5){
    for(int j=minz;j<=maxz;j+5){
        bc.biomeID = averageBiomeInArea(i,j);
        bc.x = i;
        bc.z = j;
        fwrite(&bc, sizeof(struct Biome), 1, fptr);
    }
  }
  fclose(fptr);
}

int compare_function(const void *a,const void *b) {
  int *x = (int *) a;
  int *y = (int *) b;
  return *x - *y;
}

int averageBiomeInArea(int x, int z){
  struct Biome bc;
  LayerStack g;
  setupGenerator(&g, MC_1_17);
  Layer *layer = &g.layers[L_SHORE_16];
  setLayerSeed(layer, SEED);
  int biomes[25];
  int c = 0;
  for(int i=x-2;i<=x+2;i++){
    for(int j=z-2;j<=z+2;j++){
    Pos pos = {i,j};
    biomes[c]=getBiomeAtPos(&g, pos);
    c++;
    }
  c++;
  }
  int n = sizeof(biomes);
    qsort(biomes,sizeof(biomes), sizeof(int), compare_function);
    int count = 1, popular = biomes[0], tempCount = 1, i= 0;
    for (i = 1; i < n; i++)
    {
        if (biomes[i] == biomes[i - 1])
        {
            tempCount++;
        }
        else
        {
            if (tempCount > count)
            {
                count = tempCount;
                popular = biomes[i - 1];
            }
            tempCount = 1;
        }
    }
    if (tempCount > count)
    {
        count = tempCount;
        popular = biomes[n - 1];
    }
    return popular;
}

int biomeAtCoord(int64_t x, int64_t z){
  FILE *fptr;
  fptr = fopen("out.bin","wb");
  struct Biome bc;
  LayerStack g;
  Pos pos = {x,z};
  setupGenerator(&g, MC_1_17);
  Layer *layer = &g.layers[L_SHORE_16];
  setLayerSeed(layer, SEED);
  bc.biomeID = getBiomeAtPos(&g, pos);
  bc.x = x;
  bc.z = z;
  fwrite(&bc, sizeof(struct Biome), 1, fptr);
  fclose(fptr);
}