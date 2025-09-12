[main menu](../README.md)

# Chapter 7. A set of classes

In previous chapters, variables have proliferated in the glEventListener class. This chapter will develop a series of classes that help when dealing with the multiple objects and materials that make up more complex scenes. Using these will reduce the clutter in the glEventListener class.

Figure 7.0 shows the output of the 7 programs used in Sections 7.x to 7.y.

<p align="center">
  <img src="ch7_img/ch7_1_material.png" alt="output from ch7_1_material" width="100">.<img src="ch7_img/ch7_2_model.png" alt="output from ch7_2_model" width="100">.<img src="ch7_img/ch7_3_scene1.png" alt="output from ch7_3_scene1" width="100">.<img src="ch7_img/ch7_3_scene2.png" alt="output from ch7_3_scene2" width="100">.<img src="ch7_img/ch7_3_scene3.png" alt="output from ch7_3_scene3" width="100">.<img src="ch7_img/ch7_3_scene4.png" alt="output from ch7_3_scene4" width="100">.<img src="ch7_img/ch7_3_scene5.png" alt="output from ch7_3_scene5" width="100"><br>
  <strong>Figure 7.0.</strong> Output from programs in this chapter.
</p>

The Chapter is divided into three main sections:

1. [Material](ch7_1.md)
2. [Model](ch7_2.md)
3. [Scenes](ch7_3.md)

The first section will separate out all attributes related to the material of an object, e.g. the ambient, diffuse and specular attributes and any texture maps. Some useful constants will also be defined.

The second section will group the mesh, material and transformation matrices into a single Model class. Rendering will be delegated to a separate Renderer class. In addition, a separate Light class will be developed. 

The third section shows how the classes can be used to create a series of scenes.

Figure 7.1 shows the Model, Renderer and Light classes that will be developed. 

<p align="center">
  <img src="h7_img/ch7_3_scene5.png" alt="the collection of classes" width="200"><br>
  <strong>Figure 7.1.</strong> The collection of classes (TO BE UPDATED)
</p>



[main menu](../README.md)