# JOGL and Java

## Introduction

The practical work for com3503/com4503/com6503 will involve creating an interactive 3D Computer Graphics program using Java and JOGL and learning how to use shader programs. The material given below will teach you how to install and use JOGL. Installation instructions are given for both Windows and Mac.

Learning OpenGL gives you insights into the interplay between the CPU and the GPU when developing graphics software. Other graphics APIs are available (e.g. Vulkan, WebGPU, Metal, DirectX), however, OpenGL remains relevant because of its broad support and extensive resources. It also provides a relatively easier development path for creating graphical software which means quick results can be obtained. In addition, there is still a lot of software that utilises OpenGL, e.g. Blender (UI and Eevee), Maya, Houdini, SketchUp, VTK. 

Java is used because the majority of students on the module will already have existing expertise with this language. A lot of examples on the Web will use C and OpenGL, but it is relatively easy to convert these to Java and OpenGL. However, a lot of examples on the Web use early versions of OpenGL that use a fixed function pipeline. These should be avoided. Our focus is the programmable pipeline and the use of shaders.

## Practical work

- [Chapter 1. Introduction](docs/ch1.md)
- [Chapter 2. Rendering triangles](docs/ch2.md)
- [Chapter 3. Shaders](docs/ch3.md)
- [Chapter 4. Textures](docs/ch4.md)
- [Chapter 5. Transformations, coordinate systems and a camera](docs/ch5.md)
- [Chapter 6. Lighting](docs/ch6.md)
- [Chapter 7. A set of classes](docs/ch7.md)
- [Chapter 8. Scene graphs](docs/ch8.md)
- [Chapter 9. Refinements / Extras](docs/ch9.md)
- [Chapter 10. Summary](docs/ch10.md)
- [Appendix A. Installing JOGL](docs/appendixA.md)

## Acknowledgements

Parts of this guide, particularly the early chapters, draw inspiration from Joey de Vries' excellent [learnopengl tutorial](https://learnopengl.com). His personal X handle is [https://x.com/JoeyDeVriez](https://x.com/JoeyDeVriez). The GitHub repository for the code in his tutorial: [repo](https://github.com/JoeyDeVries/LearnOpenGL.)

## Copyright

Notwithstanding the acknowledgement above, and unless otherwise noted, the copyright of the material in this guide to Java and JOGL is held by Dr Steve Maddock, University of Sheffield, 2025.