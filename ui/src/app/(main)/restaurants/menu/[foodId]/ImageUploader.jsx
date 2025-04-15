'use client';

import { useEffect, useRef, useState } from 'react';

const ImageUploader = ({
  multiple = false,
  onChange,
  defaultImages = [],
}) => {
  const [images, setImages] = useState(defaultImages);
  const [mainIndex, setMainIndex] = useState(0);
  const inputRef = useRef();

  useEffect(() => {
    if (multiple) {
      const result = images.map((img, index) => ({
        file: img.file,
        preview: img.preview,
        isMain: index === mainIndex
      }));
      onChange?.(result);
    } else {
      const singleImage = images[0]
        ? { file: images[0].file, preview: images[0].preview }
        : null;
      onChange?.(singleImage);
    }
  }, [images, mainIndex]);

  const handleUpload = (e) => {
    const files = Array.from(e.target.files);
    const newImages = files.map((file) => ({
      file,
      preview: URL.createObjectURL(file),
    }));

    if (multiple) {
      setImages((prev) => [...prev, ...newImages]);
    } else {
      setImages(newImages.slice(0, 1));
      setMainIndex(0);
    }
  };

  const removeImage = (index) => {
    const updated = images.filter((_, i) => i !== index);
    setImages(updated);
    if (index === mainIndex) {
      setMainIndex(0);
    } else if (index < mainIndex) {
      setMainIndex((prev) => prev - 1);
    }
  };

  const selectMain = (index) => {
    setMainIndex(index);
  };

  return (
    <div className="space-y-4">
      <input
        type="file"
        multiple={multiple}
        accept="image/*"
        onChange={handleUpload}
        ref={inputRef}
        className="hidden"
      />
      <button
        onClick={() => inputRef.current.click()}
        className="bg-yellow-500 text-white px-4 py-2 rounded"
      >
        Upload {multiple ? 'Images' : 'Image'}
      </button>

      {images.length > 0 && (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {images.map((img, index) => (
            <div key={index} className="relative group border rounded overflow-hidden">
              <img
                src={img.preview}
                alt={`preview-${index}`}
                className="w-full h-32 object-cover"
              />
              {multiple && (
                <button
                  onClick={() => selectMain(index)}
                  className={`absolute top-2 left-2 text-xs px-2 py-1 rounded ${
                    index === mainIndex
                      ? 'bg-green-600 text-white'
                      : 'bg-white text-black'
                  }`}
                >
                  {index === mainIndex ? 'Main' : 'Set Main'}
                </button>
              )}
              <button
                onClick={() => removeImage(index)}
                className="absolute top-2 right-2 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center text-sm opacity-80 group-hover:opacity-100"
              >
                ×
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ImageUploader;
